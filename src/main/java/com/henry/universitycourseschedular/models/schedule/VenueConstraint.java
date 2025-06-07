package com.henry.universitycourseschedular.models.schedule;

import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Venue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueConstraint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Venue venue;

    @ManyToOne
    private Department preferredDepartment;

    private boolean restricted; // if true, other departments can't use it
}
