package com.henry.universitycourseschedular.controllers.test;

import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.services.jobs.TimetableGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test/scheduler/")
@RequiredArgsConstructor @Slf4j
public class TimetableTestController {

    private final TimetableGeneratorService timetableGeneratorService;

    @GetMapping("/run")
    public TimetableDto runSchedulerTest() {
       return timetableGeneratorService.generateTimetable();
    }
}
