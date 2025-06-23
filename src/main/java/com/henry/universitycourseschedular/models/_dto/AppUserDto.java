package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUserDto {
    private String userId;
    private String fullName;
    private String emailAddress;
    private String collegeBuildingCode;
    private String departmentCode;
    private Boolean accountVerified;
    private Boolean writeAccess;
}
