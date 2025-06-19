package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.InvitationMapper;
import com.henry.universitycourseschedular.models.AppUser;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Invitation;
import com.henry.universitycourseschedular.models._dto.*;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl implements InvitationService {

    private static final int INVITE_TOKEN_LENGTH = 48;
    private static final int EXPIRATION_HOURS = 24;
    private final InvitationRepository invitationRepository;
    private final DepartmentRepository departmentRepository;
    private final AppUserRepository appUserRepository;
    private final InvitationMapper invitationMapper;
    private final EmailService emailService;
    @Value("${email.active}")
    private boolean isEmailActivated;

    @Override
    public DefaultApiResponse<InviteSuccessRequestDto> sendInviteToHod(InviteRequestDto requestBody) {
        Set<Invitation> oldInvites = invitationRepository.findAllByEmailAddress(requestBody.email());
        oldInvites.forEach(invite -> invite.setExpiredOrUsed(true));
        invitationRepository.saveAll(oldInvites);

        String inviteToken = generateInvitationToken();
        Invitation newInvitation = createAndSaveInvitation(requestBody, inviteToken);

        String inviteLink = String.format(
                "http://localhost:5173/accept-invite?token=%s&email=%s",
                inviteToken,
                requestBody.email()
        );

        Context context = prepareEmailContext( inviteLink, requestBody.email(),
                getDepartmentFromId(Long.valueOf(requestBody.departmentCode())).getCode());

        if(isEmailActivated) {
            emailService.sendEmail(
                    requestBody.email(),
                    "You're Invited: Simplify Course Allocations with Our Scheduler Tool",
                    context, "InviteHODTemplate"
            );
        }

        InviteSuccessRequestDto data = new InviteSuccessRequestDto(
                requestBody.email(),
                inviteToken,
                false,
                LocalDateTime.now(),
                newInvitation.getExpiryDate()
        );

        return buildSuccessResponse("Invite email sent successfully", StatusCodes.INVITE_SENT, data);
    }

    @Override
    public DefaultApiResponse<InviteSuccessRequestDto> validateAndAcceptInvite(String inviteToken, String hodEmail) {
        InviteValidationResult result = validateToken(inviteToken, hodEmail);

        switch (result.getStatus()) {
            case "not_found":
                return buildErrorResponse("Invite token not found.");
            case "expired":
                return buildErrorResponse("Invite link has expired.");
            case "used":
                return buildErrorResponse("Invite link has already been used or user already signed up.");
            case "valid":
                break;
            default:
                return buildErrorResponse("Unknown validation error.");
        }

        Invitation invitation = result.getInvitation();
        markInvitationAsUsed(invitation);

        InviteSuccessRequestDto data = InviteSuccessRequestDto.builder()
                .inviteToken(invitation.getToken())
                .inviteDate(LocalDateTime.now())
                .email(hodEmail)
                .inviteVerified(true)
                .expirationDate(invitation.getExpiryDate())
                .build();

        return buildSuccessResponse("Invite link approved", StatusCodes.ACTION_COMPLETED, data);
    }

    @Override
    public DefaultApiResponse<InviteResponseDto> getInvitation(String inviteToken) {
        Invitation invitation = invitationRepository.findByToken(inviteToken).orElseThrow(
                () -> new ResourceNotFoundException("Invite token not found")
        );
        return buildSuccessResponse("Invitation Found", StatusCodes.ACTION_COMPLETED, invitationMapper.toDto(invitation));
    }

    private String generateInvitationToken() {
        byte[] bytes = new byte[INVITE_TOKEN_LENGTH];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Invitation createAndSaveInvitation(InviteRequestDto request, String token) {
        Department department = departmentRepository.findByCode(request.departmentCode()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Department with code %s not found", request.departmentCode()))
        );

        Invitation invitation = Invitation.builder()
                .emailAddress(request.email())
                .department(department)
                .role(Role.HOD)
                .token(token)
                .expiredOrUsed(false)
                .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
                .build();

        return invitationRepository.save(invitation);
    }

    private InviteValidationResult validateToken(String token, String email) {
        Optional<Invitation> optionalInvite = invitationRepository.findByToken(token);

        if (optionalInvite.isEmpty()) {
            return new InviteValidationResult("not_found");
        }

        Invitation invite = optionalInvite.get();
        if (invite.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new InviteValidationResult("expired");
        }

        if (invite.isExpiredOrUsed()) {
            Optional<AppUser> userExists = appUserRepository.findByEmailAddress(email);
            if (userExists.isPresent()) {
                return new InviteValidationResult("used");
            } else {
                return new InviteValidationResult("expired");
            }
        }
        return new InviteValidationResult(invite, "valid");
    }

    private void markInvitationAsUsed(Invitation invitation) {
        invitation.setExpiredOrUsed(true);
        invitationRepository.save(invitation);
    }

    private Context prepareEmailContext(String inviteLink, String email, String department) {
        Context context = new Context();
        context.setVariable("invitationLink", inviteLink);
        context.setVariable("department", department);
        log.info("Invitation email prepared for {}", email);
        return context;
    }

    public Department getDepartmentFromId(Long departmentId){
        return departmentRepository.findById(departmentId).orElseThrow(
                () -> new ResourceNotFoundException("Department with ID: " + departmentId + " not found.")
        );
    }
}