package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.enums.CollegeBuilding;
import com.henry.universitycourseschedular.enums.Department;
import lombok.Builder;
import lombok.Data;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUserDto {
    private String emailAddress;
    private CollegeBuilding collegeBuilding;
    private Department department;
    private Boolean accountVerified;
}
