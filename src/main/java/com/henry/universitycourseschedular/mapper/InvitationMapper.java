package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models._dto.InvitationDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.invitation.Invitation;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class InvitationMapper {

    private final DepartmentRepository departmentRepository;

    public InvitationDto toDto(Invitation invitation) {
         Department department = departmentRepository.findById(Long.valueOf(invitation.getDepartmentId())).orElseThrow(
                 () -> new ResourceNotFoundException("Department not found")
         );

        return InvitationDto.builder()
                .invitationId(invitation.getInvitationId())
                .emailAddress(invitation.getEmailAddress())
                .role(invitation.getRole())
                .departmentName(department.getName())
                .token(invitation.getToken())
                .createdAt(invitation.getCreatedAt())
                .expiryDate(invitation.getExpiryDate())
                .expiredOrUsed(invitation.isExpiredOrUsed())
                .build();
    }
}
