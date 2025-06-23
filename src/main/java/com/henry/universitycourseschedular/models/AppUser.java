package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    private CollegeBuilding collegeBuilding;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountVerified = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean writeAccess = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }
}
