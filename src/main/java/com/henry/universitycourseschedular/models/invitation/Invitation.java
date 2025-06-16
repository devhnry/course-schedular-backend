package com.henry.universitycourseschedular.models.invitation;

import com.henry.universitycourseschedular.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    private String departmentId;

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    private ZonedDateTime createdAt;
    private ZonedDateTime expiryDate;

    private boolean expiredOrUsed;
}
