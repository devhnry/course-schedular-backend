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

    @Builder.Default
    private boolean restricted = false;

    public boolean isAllowed() {
        return !restricted;
    }

    public String getConstraintType() {
        return restricted ? "RESTRICTED" : "PREFERRED";
    }

    @Override
    public String toString() {
        return String.format("VenueConstraint{venue='%s', department='%s', restricted=%s}",
                venue != null ? venue.getName() : "null",
                preferredDepartment != null ? preferredDepartment.getName() : "null",
                restricted);
    }
}