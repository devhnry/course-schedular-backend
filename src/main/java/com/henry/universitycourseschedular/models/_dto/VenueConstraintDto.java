package com.henry.universitycourseschedular.models._dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueConstraintDto {
    private Long venueId;
    private Long departmentId;
    private boolean restricted;
    private String venueName;
    private String departmentName;
    private String constraintType;
}
