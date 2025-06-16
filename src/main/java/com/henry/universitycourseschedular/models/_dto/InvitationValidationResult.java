package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.models.invitation.Invitation;
import lombok.Data;

@Data
public class InvitationValidationResult {

    private final String status; // "valid", "expired", "used", "not_found"
    private Invitation invitation;

    public InvitationValidationResult(String status) {
        this.status = status;
    }

    public InvitationValidationResult(Invitation invitation, String status) {
        this.invitation = invitation;
        this.status = status;
    }

}
