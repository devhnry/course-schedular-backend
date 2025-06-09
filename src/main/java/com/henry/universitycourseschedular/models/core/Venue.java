package com.henry.universitycourseschedular.models.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int capacity;

    @Builder.Default
    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private CollegeBuilding collegeBuilding;
}