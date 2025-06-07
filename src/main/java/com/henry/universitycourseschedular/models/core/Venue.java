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

    @ManyToOne(fetch = FetchType.LAZY)
    private CollegeBuilding collegeBuilding;
}