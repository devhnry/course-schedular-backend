package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.InviteHodDto;
import com.henry.universitycourseschedular.dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.entity.Invitation;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import com.henry.universitycourseschedular.services.EmailService;
import com.henry.universitycourseschedular.services.InvitationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;

@Service
@RequiredArgsConstructor @Slf4j
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
    }

    @Override
    public DefaultApiResponse<SuccessfulInviteDto>  sendHodInvite(InviteHodDto requestBody) {
        DefaultApiResponse<SuccessfulInviteDto> response = new DefaultApiResponse<>();
        SuccessfulInviteDto data = new SuccessfulInviteDto();

        Set<Invitation> invitations = invitationRepository.findAllByEmailAddress(requestBody.getEmail());
        invitations.forEach(invitation -> invitation.setExpiredOrUsed(true));

        String inviteToken = generateInvitationToken();
        Invitation invitation = createInvitation(requestBody, inviteToken);

        String inviteLink =
                "https://localhost:6050/api/v1/accept-invite?token=" + inviteToken + "&email=" + requestBody.getEmail();
        Context context = generateEmailContext(inviteLink, requestBody.getEmail());

        emailService.sendEmail(requestBody.getEmail(), "You're Invited: Simplify Course Allocations with Our Scheduler Tool", context, "inviteEmailTemplate");

        data.setEmail(requestBody.getEmail());
        data.setInviteToken(inviteToken);
        data.setInviteVerified(false);
        data.setInviteDate(LocalDate.from(LocalDateTime.now()));
        data.setExpirationDate(invitation.getExpiryDate());

        response.setStatusCode(00);
        response.setStatusMessage("Email Sent Successfully");
        response.setData(data);

        return response;
    }

    @Override
    public DefaultApiResponse<SuccessfulInviteDto> approveInvite(String inviteToken, String hodEmail) {
        HttpSession session = getCurrentRequest().getSession();

        DefaultApiResponse<SuccessfulInviteDto> response = new DefaultApiResponse<>();

        SuccessfulInviteDto data = new SuccessfulInviteDto();
        Invitation validatedInvite = validateToken(inviteToken);

        if(validatedInvite != null){
            markUsed(validatedInvite);

            data.setEmail(hodEmail);
            data.setInviteVerified(true);

            session.setAttribute("inviteVerified", true);
            session.setAttribute("inviteEmail", hodEmail);

            response.setStatusCode(00);
            response.setStatusMessage("Invite Link Approved");
            response.setData(data);
            return response;
        }

        response.setStatusCode(99);
        response.setStatusMessage("Invite Link Expired");
        return response;
    }

    private String generateInvitationToken() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Invitation createInvitation(InviteHodDto request, String token) {
        Invitation invitation = Invitation.builder()
                .emailAddress(request.getEmail())
                .role(Role.HOD)
                .department(request.getDepartment())
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24-hour expiry
                .expiredOrUsed(false)
                .build();

        return invitationRepository.save(invitation);
    }

    private Invitation validateToken(String token) {
        return invitationRepository.findByToken(token)
                .filter(inv -> !inv.isExpiredOrUsed() && inv.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(null);
    }

    private void markUsed(Invitation invitation) {
        invitation.setExpiredOrUsed(true);
        invitationRepository.save(invitation);
    }

    private Context generateEmailContext(String inviteLink, String email){
        Context emailContext = new Context();
        emailContext.setVariable("inviteLink", inviteLink);

        log.info("Email Invite has been sent to {}", email);
        return emailContext;
    }
}
