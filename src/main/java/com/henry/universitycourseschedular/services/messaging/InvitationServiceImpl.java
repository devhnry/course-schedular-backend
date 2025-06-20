package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InviteHodDto;
import com.henry.universitycourseschedular.models._dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.invitation.Invitation;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
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
    private final EmailService emailService;
    @Value("${email.active}")
    private boolean isEmailActivated;

    @Override
    public DefaultApiResponse<SuccessfulInviteDto> sendInviteToHod(InviteHodDto requestBody) {
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
                getDepartmentFromId(Long.valueOf(requestBody.departmentId())).getCode());

        if(isEmailActivated) {
            emailService.sendEmail(
                    requestBody.email(),
                    "You're Invited: Simplify Course Allocations with Our Scheduler Tool",
                    context, "InviteHODTemplate"
            );
        }

        SuccessfulInviteDto data = new SuccessfulInviteDto(
                requestBody.email(),
                inviteToken,
                false,
                ZonedDateTime.now(),
                newInvitation.getExpiryDate()
        );

        return buildSuccessResponse("Invite email sent successfully", StatusCodes.INVITE_SENT, data);
    }

    @Override
    public DefaultApiResponse<SuccessfulInviteDto> validateAndAcceptInvite(String inviteToken, String hodEmail) {
        Optional<Invitation> optionalInvitation = validateToken(inviteToken);

        if (optionalInvitation.isEmpty()) {
            return buildErrorResponse("Invite link is invalid or has expired.");
        }

        Invitation invitation = optionalInvitation.get();
        markInvitationAsUsed(invitation);

        SuccessfulInviteDto data = SuccessfulInviteDto.builder()
                .email(hodEmail)
                .inviteVerified(true)
                .build();

        return buildSuccessResponse("Invite link approved", StatusCodes.ACTION_COMPLETED, data);
    }

    @Override
    public DefaultApiResponse<Invitation> getInvitation(String inviteToken) {
        Invitation invitation = invitationRepository.findByToken(inviteToken).orElseThrow(
                () -> new ResourceNotFoundException("Invite token not found")
        );
        return buildSuccessResponse("Invitation Found", StatusCodes.ACTION_COMPLETED, invitation);
    }

    private String generateInvitationToken() {
        byte[] bytes = new byte[INVITE_TOKEN_LENGTH];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Invitation createAndSaveInvitation(InviteHodDto request, String token) {
        Invitation invitation = Invitation.builder()
                .emailAddress(request.email())
                .departmentId(request.departmentId())
                .role(Role.HOD)
                .token(token)
                .expiredOrUsed(false)
                .expiryDate(ZonedDateTime.now().plusHours(EXPIRATION_HOURS))
                .build();

        return invitationRepository.save(invitation);
    }

    private Optional<Invitation> validateToken(String token) {
        return invitationRepository.findByToken(token)
                .filter(invite -> !invite.isExpiredOrUsed() && invite.getExpiryDate().isAfter(ZonedDateTime.now()));
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