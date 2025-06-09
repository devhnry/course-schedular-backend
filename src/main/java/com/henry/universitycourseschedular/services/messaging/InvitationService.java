package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InviteHodDto;
import com.henry.universitycourseschedular.models._dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.models.invitation.Invitation;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    DefaultApiResponse<SuccessfulInviteDto> sendInviteToHod(InviteHodDto requestBody);
    DefaultApiResponse<SuccessfulInviteDto> validateAndAcceptInvite(String inviteToken, String hodEmail);

    DefaultApiResponse<Invitation> getInvitation(String inviteToken);
}
