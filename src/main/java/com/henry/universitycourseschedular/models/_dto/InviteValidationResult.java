package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.models.Invitation;
import lombok.Data;
import lombok.Getter;

@Data @Getter
public class InviteValidationResult {

    private final String status; // "valid", "expired", "used", "not_found"
    private Invitation invitation;

    public InviteValidationResult(String status) {
        this.status = status;
    }

    public InviteValidationResult(Invitation invitation, String status) {
        this.invitation = invitation;
        this.status = status;
    }
}
