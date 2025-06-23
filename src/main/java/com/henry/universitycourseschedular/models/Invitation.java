package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    private boolean expiredOrUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "code")
    private Department department;
}
