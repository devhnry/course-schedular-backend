package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InvitationDto;
import com.henry.universitycourseschedular.models._dto.InviteHodDto;
import com.henry.universitycourseschedular.models._dto.SuccessfulInviteDto;
import com.henry.universitycourseschedular.services.messaging.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/send")
    public ResponseEntity<DefaultApiResponse<SuccessfulInviteDto>> sendEmailInvite
            (@RequestBody @Validated InviteHodDto requestBody){
        DefaultApiResponse<SuccessfulInviteDto> response = invitationService.sendInviteToHod(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<DefaultApiResponse<SuccessfulInviteDto>> processEmailInvite
            (@RequestParam("token") String inviteToken, @RequestParam("hodEmail") String hodEmail){
        DefaultApiResponse<SuccessfulInviteDto> response = invitationService.validateAndAcceptInvite(inviteToken, hodEmail);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/details")
    public ResponseEntity<DefaultApiResponse<InvitationDto>> getInviteDetails(@RequestParam("token") String inviteToken){
        DefaultApiResponse<InvitationDto> response = invitationService.getInvitation(inviteToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
