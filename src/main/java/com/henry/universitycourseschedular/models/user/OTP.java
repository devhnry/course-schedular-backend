package com.henry.universitycourseschedular.models.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
