package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InvitationDto;
import com.henry.universitycourseschedular.models._dto.InviteHodDto;
import com.henry.universitycourseschedular.models._dto.SuccessfulInviteDto;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    DefaultApiResponse<SuccessfulInviteDto> sendInviteToHod(InviteHodDto requestBody);
    DefaultApiResponse<SuccessfulInviteDto> validateAndAcceptInvite(String inviteToken, String hodEmail);
    DefaultApiResponse<InvitationDto> getInvitation(String inviteToken);
}
