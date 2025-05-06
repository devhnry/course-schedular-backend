package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String invitationId;

    private String emailAddress;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Column(nullable = false, unique = true)
    private String token;

    private ZonedDateTime expiryDate;

    private boolean expiredOrUsed;
}
