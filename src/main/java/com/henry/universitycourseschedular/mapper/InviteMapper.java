package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Invitation;
import com.henry.universitycourseschedular.models._dto.InviteRequestDto;
import com.henry.universitycourseschedular.models._dto.InviteResponseDto;
import org.springframework.stereotype.Component;

@Component
public class InviteMapper {

    public Invitation fromDto(InviteRequestDto dto, Department department) {
        return Invitation.builder()
                .department(department)
                .expiredOrUsed(false)
                .build();
    }

    public InviteResponseDto toDto(Invitation invitation) {
        return new InviteResponseDto (
                invitation.getInvitationId(),
                invitation.getEmailAddress(),
                String.valueOf(invitation.getRole()),
                invitation.getDepartment().getCode(),
                invitation.getDepartment().getName(),
                invitation.isExpiredOrUsed(),
                invitation.getCreatedAt(),
                invitation.getExpiryDate()
        );
    }

}
