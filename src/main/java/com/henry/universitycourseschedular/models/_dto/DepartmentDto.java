package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDto {
        private Long id;
        private String name;
        private String code;
        private Long collegeBuildingId;
        private String collegeBuildingName;
}
