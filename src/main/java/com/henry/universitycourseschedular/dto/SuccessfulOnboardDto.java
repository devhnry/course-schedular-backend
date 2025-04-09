package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.entity.AppUser;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulOnboardDto {
    private String emailAddress;
    private boolean accountVerified;
    private String oneTimePassword;
}
