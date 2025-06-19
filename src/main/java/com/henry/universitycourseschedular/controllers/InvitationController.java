package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.InviteRequestDto;
import com.henry.universitycourseschedular.models._dto.InviteResponseDto;
import com.henry.universitycourseschedular.models._dto.InviteSuccessRequestDto;
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
    public ResponseEntity<DefaultApiResponse<InviteSuccessRequestDto>> sendEmailInvite
            (@RequestBody @Validated InviteRequestDto requestBody){
        DefaultApiResponse<InviteSuccessRequestDto> response = invitationService.sendInviteToHod(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<DefaultApiResponse<InviteSuccessRequestDto>> processEmailInvite
            (@RequestParam("token") String inviteToken, @RequestParam("hodEmail") String hodEmail){
        DefaultApiResponse<InviteSuccessRequestDto> response = invitationService.validateAndAcceptInvite(inviteToken, hodEmail);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/details")
    public ResponseEntity<DefaultApiResponse<InviteResponseDto>> getInviteDetails(@RequestParam("token") String inviteToken){
        DefaultApiResponse<InviteResponseDto> response = invitationService.getInvitation(inviteToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
