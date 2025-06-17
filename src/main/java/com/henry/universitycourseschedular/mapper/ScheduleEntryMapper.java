package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.ScheduleEntryDto;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEntryMapper {

    public ScheduleEntryDto mapToDTO(ScheduleEntry entry) {
        CourseAssignment ca = entry.getCourseAssignment();
        return new ScheduleEntryDto(
                ca.getCourse().getCourseCode(),
                ca.getLecturer().getFirstName(),
                entry.getVenue().getName(),
                entry.getTimeSlot().getDayOfWeek().name(),
                entry.getTimeSlot().getStartTime(),
                entry.getTimeSlot().getEndTime()
        );
    }
}

