package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimetableResponseDto {
    private String departmentName;
    private String programCode;
    private Map<String, List<DayScheduleDto>> weeklySchedule; // Key: Day name (MONDAY, TUESDAY, etc.)
    private TimetableStats stats;

    @Data
    @Builder
    public static class DayScheduleDto {
        private String day;
        private List<TimeSlotScheduleDto> timeSlots;
    }

    @Data
    @Builder
    public static class TimeSlotScheduleDto {
        private LocalTime startTime;
        private LocalTime endTime;
        private String timeRange; // "08:00 - 09:00"
        private List<VenueScheduleDto> venues;
    }

    @Data
    @Builder
    public static class VenueScheduleDto {
        private String building;
        private String roomNo;
        private String fullVenueName;
        private List<String> courseCodes;
        private List<String> lecturerNames;
    }

    @Data
    @Builder
    public static class TimetableStats {
        private int totalAssignments;
        private int successfulAssignments;
        private int failedAssignments;
        private double successRate;
        private List<String> unassignedCourses;
    }
}
