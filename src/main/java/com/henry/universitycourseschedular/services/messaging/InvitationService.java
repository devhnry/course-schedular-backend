package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InviteRequestDto;
import com.henry.universitycourseschedular.models._dto.InviteResponseDto;
import com.henry.universitycourseschedular.models._dto.InviteSuccessRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    DefaultApiResponse<InviteSuccessRequestDto> sendInviteToHod(InviteRequestDto requestBody);
    DefaultApiResponse<InviteSuccessRequestDto> validateAndAcceptInvite(String inviteToken, String hodEmail);
    DefaultApiResponse<InviteResponseDto> getInvitation(String inviteToken);
}
