package com.henry.universitycourseschedular.controllers.test;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.services.jobs.TimetableGeneratorService;
import com.henry.universitycourseschedular.utils.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/timetable")
@RequiredArgsConstructor
@Slf4j
public class TimetableTestController {

    private final TimetableGeneratorService timetableGenerator;

    @GetMapping("/generate")
    public DefaultApiResponse<TimetableDto> generateFullTimetable() {
        try {
            log.info("Starting full timetable generation test");
            TimetableDto result = timetableGenerator.generateTimetable();

            return ApiResponseUtil.buildSuccessResponse(
                    "Timetable generated successfully",
                    StatusCodes.SCHEDULE_GENERATED,
                    result
            );
        } catch (Exception e) {
            log.error("Timetable generation test failed", e);
            return ApiResponseUtil.buildErrorResponse(
                    "Timetable generation failed: " + e.getMessage(),
                    StatusCodes.SCHEDULE_GENERATION_ERROR
            );
        }
    }
}
