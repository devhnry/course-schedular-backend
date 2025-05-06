package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Title;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecturer{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String lecturerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Title title;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
