package com.henry.universitycourseschedular.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken {
    @Id
    private String tokenId;

    @Column(nullable = false,length = 500)
    private String accessToken;

    @Column(nullable = false,length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private Boolean expiredOrRevoked;

    @Column(nullable = false)
    private Instant expiresAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "userId", referencedColumnName = "userId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AppUser user;
}
