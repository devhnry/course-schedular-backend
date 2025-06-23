package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models.CourseAssignment;
import com.henry.universitycourseschedular.models.ScheduleEntry;
import com.henry.universitycourseschedular.models._dto.ScheduleEntryDto;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEntryMapper {

    public ScheduleEntryDto mapToDTO(ScheduleEntry entry) {
        CourseAssignment ca = entry.getCourseAssignment();
        return new ScheduleEntryDto(
                ca.getCourse().getCode(),
                ca.getLecturers(),
                entry.getVenue().getName(),
                entry.getTimeSlot().getDayOfWeek().name(),
                entry.getTimeSlot().getStartTime(),
                entry.getTimeSlot().getEndTime()
        );
    }
}

