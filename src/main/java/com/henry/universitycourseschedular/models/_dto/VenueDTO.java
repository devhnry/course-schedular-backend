package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VenueDTO {
    private String name;              // e.g., "LT 1"
    private int capacity;             // e.g., 120
    private boolean available;
    private String collegeCode;       // Used to fetch the associated CollegeBuilding
}
