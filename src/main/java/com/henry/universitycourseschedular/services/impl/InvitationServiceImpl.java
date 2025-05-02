package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.InviteHodDto;
import com.henry.universitycourseschedular.dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.entity.Invitation;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import com.henry.universitycourseschedular.services.EmailService;
import com.henry.universitycourseschedular.services.InvitationService;
import com.henry.universitycourseschedular.utils.ApiResponseUtil;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;

    private static final int INVITE_TOKEN_LENGTH = 48;
    private static final int EXPIRATION_HOURS = 24;

    @Value("${email.active}")
    private boolean isEmailActivated;

    @Override
    public DefaultApiResponse<SuccessfulInviteDto> sendInviteToHod(InviteHodDto requestBody) {
        Set<Invitation> oldInvites = invitationRepository.findAllByEmailAddress(requestBody.getEmail());
        oldInvites.forEach(invite -> invite.setExpiredOrUsed(true));
        invitationRepository.saveAll(oldInvites);

        String inviteToken = generateInvitationToken();
        Invitation newInvitation = createAndSaveInvitation(requestBody, inviteToken);

        String inviteLink = String.format(
                "http://localhost:5173/accept-invite?token=%s&email=%s",
                inviteToken,
                requestBody.getEmail()
        );

        Context context = prepareEmailContext(inviteLink, requestBody.getEmail(),
                requestBody.getDepartment().toString());
        if(isEmailActivated) {
            emailService.sendEmail(
                    requestBody.getEmail(), "You're Invited: Simplify Course Allocations with Our Scheduler Tool", context, "InviteHODTemplate"
            );
        }

        SuccessfulInviteDto data = new SuccessfulInviteDto();
        data.setEmail(requestBody.getEmail());
        data.setInviteToken(inviteToken);
        data.setInviteVerified(false);
        data.setInviteDate(ZonedDateTime.now());
        data.setExpirationDate(newInvitation.getExpiryDate());

        return buildSuccessResponse("Invite email sent successfully", data);
    }

    @Override
    public DefaultApiResponse<SuccessfulInviteDto> validateAndAcceptInvite(String inviteToken, String hodEmail) {
        Optional<Invitation> optionalInvitation = validateToken(inviteToken);

        if (optionalInvitation.isEmpty()) {
            return buildFailureResponse("Invite link is invalid or has expired.");
        }

        Invitation invitation = optionalInvitation.get();
        markInvitationAsUsed(invitation);

        SuccessfulInviteDto data = new SuccessfulInviteDto();
        data.setEmail(hodEmail);
        data.setInviteVerified(true);

        return ApiResponseUtil.buildSuccessResponse("Invite link approved", StatusCodes.ACTION_COMPLETED, data);
    }

    @Override
    public DefaultApiResponse<Invitation> getInvitation(String inviteToken) {
        Invitation invitation = invitationRepository.findByToken(inviteToken).orElseThrow(
                () -> new RuntimeException("Invite token not found")
        );

        return ApiResponseUtil.buildSuccessResponse("Invitation Found", StatusCodes.ACTION_COMPLETED, invitation);
    }

    private String generateInvitationToken() {
        byte[] bytes = new byte[INVITE_TOKEN_LENGTH];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Invitation createAndSaveInvitation(InviteHodDto request, String token) {
        Invitation invitation = Invitation.builder()
                .emailAddress(request.getEmail())
                .department(request.getDepartment())
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

    private DefaultApiResponse<SuccessfulInviteDto> buildSuccessResponse(String message, SuccessfulInviteDto data) {
        DefaultApiResponse<SuccessfulInviteDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.INVITE_SENT);
        response.setStatusMessage(message);
        response.setData(data);
        return response;
    }

    private DefaultApiResponse<SuccessfulInviteDto> buildFailureResponse(String message) {
        DefaultApiResponse<SuccessfulInviteDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.GENERIC_FAILURE);
        response.setStatusMessage(message);
        return response;
    }
}