package com.henry.universitycourseschedular.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class OneTimePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long otpId;

    @Column(nullable = false)
    private String oneTimePassword;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expirationTime;

    @Column(nullable = false)
    private boolean expired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser createdFor;
}
