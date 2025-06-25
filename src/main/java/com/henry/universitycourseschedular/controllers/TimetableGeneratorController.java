package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.services.jobs.TimetableGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableGeneratorController {

    private final TimetableGeneratorService timetableGeneratorService;

    @GetMapping("/generate")
    public ResponseEntity<DefaultApiResponse<TimetableDto>> generateTimetable(TimetableDto timetableDto) {
        DefaultApiResponse<TimetableDto> body = null;
        try {
            TimetableDto result = timetableGeneratorService.generateTimetable();
            body = new DefaultApiResponse<>();
            body.setStatusCode(0);
            body.setStatusMessage("Timetable Generated");
            body.setData(result);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(body);
    }

}
