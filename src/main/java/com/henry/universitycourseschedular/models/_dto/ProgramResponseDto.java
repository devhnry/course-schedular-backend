package com.henry.universitycourseschedular.models._dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponseDto {
    private Long id;
    private String name;
    private String code;
    private String departmentName;
    private String departmentCode;
}
