package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.enums.Title;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LecturerDto {
    @NotBlank(message = "First name is Required")
    private String firstName;

    @NotBlank(message = "Last name is Required")
    private String lastName;

    @NotBlank(message = "Title is Required")
    @Enumerated(EnumType.STRING)
    private Title title;
}
