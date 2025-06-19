package com.henry.universitycourseschedular.controllers.test;

import com.henry.universitycourseschedular.enums.Title;
import com.henry.universitycourseschedular.mapper.ScheduleEntryMapper;
import com.henry.universitycourseschedular.models.*;
import com.henry.universitycourseschedular.models._dto.ScheduleEntryDto;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import com.henry.universitycourseschedular.services.jobs.GreedySchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/scheduler/")
@RequiredArgsConstructor
public class SchedulerTestController {

    private final GreedySchedulerService scheduler;
    private final TimeSlotRepository timeSlotRepository;
    private final VenueRepository venueRepository;
    private final ScheduleEntryMapper mapper;

    @GetMapping("/run")
    public List<ScheduleEntryDto> runSchedulerTest() {
        List<TimeSlot> slots = timeSlotRepository.findAll();
        List<Venue> venues = venueRepository.findAll();
        List<CourseAssignment> mockedAssignments = createMockAssignments();
        return scheduler.assignCourses(mockedAssignments, slots, venues)
                .stream()
                .map(mapper::mapToDTO)
                .toList();
    }


    private List<CourseAssignment> createMockAssignments() {
        College cst = new College(1L, "College of Science and Technology", "CST");

        CollegeBuilding mockCST = new CollegeBuilding(1L, "College Of Science and Technology", "CST", cst);

        Department mockCIS = new Department(1L, "Computer and Information Science", "CIS", mockCST);
        Department mockARCH = new Department(2L, "Architecture", "ARCH", mockCST);
        Department mockEM = new Department(3L, "EstateManagement", "EM", mockCST);
        Department mockAldcDept =  new Department(4L, "ALDC", "ALDC", mockCST);

        Program mockComputerScience = new Program(1L, "Computer Science", mockCIS);
        Program mockArchitecture = new Program(2L, "Architecture", mockARCH);
        Program mockAldc = new Program(3L, "Aldc", mockAldcDept);

        GeneralBody mockGeneralBody = new GeneralBody(1L, "ALDC", "Leadership");

        Lecturer mockLect1 = new Lecturer(1L, Title.MR, "John Doe");
        Lecturer mockLect2 = new Lecturer(2L, Title.MR, "Smith Sarah");
        Lecturer mockLect3 = new Lecturer(3L, Title.MR, "Rose Mary");
        Lecturer mockLect4 = new Lecturer(4L, Title.MR, "Bot Peter");

        Course DLD211 = new Course(1L, "DLD211", "Leadership and Dev Studies", 200, 2, null, mockGeneralBody, 1000);
        Course TMC411 = new Course(2L, "TMC411", "Total Man Concept", 400, 2, null, mockGeneralBody, 1000);
        Course CIS421 = new Course(3L, "CIS421", "Computer Security", 400, 2, mockComputerScience, null, 120);
        Course ARC231 = new Course(4L, "ARC231", "Architecture Course", 200, 2, mockArchitecture, null, 80);
        Course ARC241 = new Course(5L, "ARC241", "Architecture Course 2", 200, 2, mockArchitecture, null, 70);
        Course CSC431 = new Course(6L, "CSC431", "Computer Architecture", 400, 2, mockComputerScience, null, 130);

        return List.of(
                // General Course - DLD
                new CourseAssignment(1L, DLD211, mockLect1, null, null, true),

                // General Course - TMC
                new CourseAssignment(2L, TMC411, mockLect2, null, null,  true),

                // Departmental Course - Security
                new CourseAssignment(3L, CIS421, mockLect3, mockComputerScience, mockCIS,  false),

                // Departmental Course - Architecture
                new CourseAssignment(4L, ARC231, mockLect2, mockArchitecture, mockARCH,  false),

                // Departmental Course - Architecture 2
                new CourseAssignment(5L, ARC241, mockLect2, mockArchitecture, mockARCH, false),

                // Departmental Course - Computer Architecture
                new CourseAssignment(6L, CSC431, mockLect4, mockComputerScience, mockCIS, false)
        );
    }

}
