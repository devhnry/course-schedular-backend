package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.InviteHodDto;
import com.henry.universitycourseschedular.dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.entity.Invitation;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    DefaultApiResponse<SuccessfulInviteDto> sendInviteToHod(InviteHodDto requestBody);
    DefaultApiResponse<SuccessfulInviteDto> validateAndAcceptInvite(String inviteToken, String hodEmail);

    DefaultApiResponse<Invitation> getInvitation(String inviteToken);
}
