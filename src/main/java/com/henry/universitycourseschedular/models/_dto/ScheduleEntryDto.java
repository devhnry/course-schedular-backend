package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleEntryDto {
    private String courseCode;
    private String courseName;
    private String lecturerName;
    private String timeSlot;
    private String venueName;
    private String departmentCode;
    private String programCode;
}
