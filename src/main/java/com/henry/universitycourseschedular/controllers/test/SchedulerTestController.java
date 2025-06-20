package com.henry.universitycourseschedular.controllers.test;

import com.henry.universitycourseschedular.mapper.ScheduleEntryMapper;
import com.henry.universitycourseschedular.models.*;
import com.henry.universitycourseschedular.models._dto.ScheduleEntryDto;
import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import com.henry.universitycourseschedular.services.jobs.GACConstraintSolverService;
import com.henry.universitycourseschedular.services.jobs.GreedySchedulerService;
import com.henry.universitycourseschedular.services.jobs.SimulatedAnnealingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/test/scheduler/")
@RequiredArgsConstructor @Slf4j
public class SchedulerTestController {

    private final GreedySchedulerService scheduler;
    private final TimeSlotRepository timeSlotRepository;
    private final SimulatedAnnealingService annealer;
    private final GACConstraintSolverService gac;
    private final GreedySchedulerService greedyScheduler;
    private final VenueRepository venueRepository;
    private final ScheduleEntryMapper mapper;

    @GetMapping("/run")
    public TimetableDto runSchedulerTest() {
        List<TimeSlot> slots = timeSlotRepository.findAll();
        List<Venue> venues = venueRepository.findAll();
        List<CourseAssignment> courses = createMockAssignments();

        // Phase 1: Greedy scheduling (handles hard constraints)
        log.info("Phase 1: Running Greedy Scheduler");
        List<ScheduleEntry> greedyResult = greedyScheduler.assignCourses(courses, slots, venues);
        log.info("Greedy scheduler assigned {} out of {} courses",
                greedyResult.size(), courses.size());

        if (greedyResult.isEmpty()) {
            log.warn("Greedy scheduler produced no assignments");
            return TimetableDto.builder()
                    .departmentName("No assignments")
                    .programCode("ERROR")
                    .days(List.of())
                    .build();
        }

        // Phase 2: Simulated Annealing (optimization)
        log.info("Phase 2: Running Simulated Annealing Optimization");
        List<ScheduleEntry> optimizedResult = annealer.optimize(greedyResult);
        log.info("Simulated annealing optimized {} assignments", optimizedResult.size());

        // Phase 3: GAC (constraint consistency)
        log.info("Phase 3: Running GAC Constraint Solver");
        List<ScheduleEntry> finalResult = gac.enforceConstraints(optimizedResult);
        log.info("GAC produced {} consistent assignments", finalResult.size());

        Schedule schedule = new Schedule();
        schedule.setEntries(finalResult);

        log.info("Timetable generation completed successfully with {} final assignments",
                finalResult.size());

        List<ScheduleEntryDto> scheduleEntryDtos = finalResult.stream()
                .map(entry -> new ScheduleEntryDto(
                        entry.getCourseAssignment().getCourse().getCode(),
                        entry.getCourseAssignment().getLecturers(),
                        entry.getVenue().getName(),
                        entry.getTimeSlot().getDayOfWeek().name(),
                        entry.getTimeSlot().getStartTime(),
                        entry.getTimeSlot().getEndTime()
                ))
                .collect(Collectors.toList());

        String departmentName = "Generated Schedule";
        String programCode = "ALL";

        if (!finalResult.isEmpty()) {
            ScheduleEntry firstEntry = finalResult.get(0);
            if (firstEntry.getCourseAssignment().getCourse().getProgram() != null) {
                departmentName = firstEntry.getCourseAssignment().getCourse().getProgram().getDepartment().getName();
                programCode = firstEntry.getCourseAssignment().getCourse().getProgram().getName();
            }
        }

        return TimetableDto.builder()
                .departmentName(departmentName)
                .programCode(programCode)
                .days(scheduleEntryDtos)
                .build();
    }


    private List<CourseAssignment> createMockAssignments() {
        // === COLLEGES ===
        College cst = new College(1L, "College of Science and Technology", "CST");
        College cmss = new College(2L, "College of Management and Social Sciences", "CMSS");
        College clds = new College(3L, "College of Leadership Development Studies", "CLDS");
        College coe = new College(4L, "College of Engineering", "COE");

        // === COLLEGE BUILDINGS ===
        CollegeBuilding cstBuilding = new CollegeBuilding(1L, "CST Building", "CST-BLDG", cst);
        CollegeBuilding cmssBuilding = new CollegeBuilding(2L, "CMSS Building", "CMSS-BLDG", cmss);
        CollegeBuilding cldsBuilding = new CollegeBuilding(3L, "CLDS Building", "CLDS-BLDG", clds);
        CollegeBuilding coeBuilding = new CollegeBuilding(4L, "COE Building", "COE-BLDG", coe);

        // === DEPARTMENTS ===
        // CST Departments
        Department cisDept = new Department(1L, "Computer and Information Science", "CIS", cstBuilding);
        Department archDept = new Department(2L, "Architecture", "ARCH", cstBuilding);
        Department emDept = new Department(3L, "Estate Management", "EM", cstBuilding);
        Department mathDept = new Department(4L, "Mathematics", "MATH", cstBuilding);
        Department physicsDept = new Department(5L, "Physics", "PHYS", cstBuilding);
        Department chemDept = new Department(6L, "Chemistry", "CHEM", cstBuilding);

        // CMSS Departments
        Department accDept = new Department(7L, "Accounting", "ACC", cmssBuilding);
        Department busDept = new Department(8L, "Business Administration", "BUS", cmssBuilding);
        Department econDept = new Department(9L, "Economics", "ECON", cmssBuilding);
        Department massDept = new Department(10L, "Mass Communication", "MASS", cmssBuilding);

        // CLDS Departments
        Department aldcDept = new Department(11L, "ALDC", "ALDC", cldsBuilding);
        Department psDept = new Department(12L, "Political Science", "PS", cldsBuilding);

        // COE Departments
        Department civilDept = new Department(13L, "Civil Engineering", "CIVIL", coeBuilding);
        Department mechDept = new Department(14L, "Mechanical Engineering", "MECH", coeBuilding);
        Department elecDept = new Department(15L, "Electrical Engineering", "ELEC", coeBuilding);

        // === PROGRAMS ===
        // CST Programs
        Program computerScience = new Program(1L, "Computer Science", cisDept);
        Program informationTech = new Program(2L, "Information Technology", cisDept);
        Program architecture = new Program(3L, "Architecture", archDept);
        Program estateManagement = new Program(4L, "Estate Management", emDept);
        Program mathematics = new Program(5L, "Mathematics", mathDept);
        Program physics = new Program(6L, "Physics", physicsDept);
        Program chemistry = new Program(7L, "Chemistry", chemDept);

        // CMSS Programs
        Program accounting = new Program(8L, "Accounting", accDept);
        Program businessAdmin = new Program(9L, "Business Administration", busDept);
        Program economics = new Program(10L, "Economics", econDept);
        Program massCommunication = new Program(11L, "Mass Communication", massDept);

        // CLDS Programs
        Program aldc = new Program(12L, "ALDC", aldcDept);
        Program politicalScience = new Program(13L, "Political Science", psDept);

        // COE Programs
        Program civilEng = new Program(14L, "Civil Engineering", civilDept);
        Program mechEng = new Program(15L, "Mechanical Engineering", mechDept);
        Program elecEng = new Program(16L, "Electrical Engineering", elecDept);

        // === GENERAL BODY ===
//        GeneralBody generalBody = new GeneralBody(1L, "General Studies", "General");

        // === LECTURERS ===
        // CIS Lecturers
        Lecturer lect1 = new Lecturer(1L, "Dr. John Smith", cisDept);
        Lecturer lect2 = new Lecturer(2L, "Prof. Sarah Johnson", cisDept);
        Lecturer lect3 = new Lecturer(3L, "Dr. Michael Brown", cisDept);
        Lecturer lect4 = new Lecturer(4L, "Dr. Emily Davis", cisDept);
        Lecturer lect5 = new Lecturer(5L, "Prof. David Wilson", cisDept);

        // Architecture Lecturers
        Lecturer lect6 = new Lecturer(6L, "Dr. James Miller", archDept);
        Lecturer lect7 = new Lecturer(7L, "Prof. Lisa Anderson", archDept);
        Lecturer lect8 = new Lecturer(8L, "Dr. Robert Taylor", archDept);

        // Estate Management Lecturers
        Lecturer lect9 = new Lecturer(9L, "Dr. Mary Thomas", emDept);
        Lecturer lect10 = new Lecturer(10L, "Prof. William Jackson", emDept);

        // Math Lecturers
        Lecturer lect11 = new Lecturer(11L, "Dr. Jennifer White", mathDept);
        Lecturer lect12 = new Lecturer(12L, "Prof. Christopher Harris", mathDept);

        // Physics Lecturers
        Lecturer lect13 = new Lecturer(13L, "Dr. Amanda Martin", physicsDept);
        Lecturer lect14 = new Lecturer(14L, "Prof. Daniel Thompson", physicsDept);

        // Chemistry Lecturers
        Lecturer lect15 = new Lecturer(15L, "Dr. Michelle Garcia", chemDept);
        Lecturer lect16 = new Lecturer(16L, "Prof. Kevin Martinez", chemDept);

        // Business Lecturers
        Lecturer lect17 = new Lecturer(17L, "Dr. Patricia Robinson", accDept);
        Lecturer lect18 = new Lecturer(18L, "Prof. Mark Clark", busDept);
        Lecturer lect19 = new Lecturer(19L, "Dr. Linda Rodriguez", econDept);
        Lecturer lect20 = new Lecturer(20L, "Prof. Steven Lewis", massDept);

        // ALDC Lecturers
        Lecturer lect21 = new Lecturer(21L, "Dr. Barbara Lee", aldcDept);
        Lecturer lect22 = new Lecturer(22L, "Prof. Paul Walker", aldcDept);

        // Engineering Lecturers
        Lecturer lect23 = new Lecturer(23L, "Dr. Nancy Hall", civilDept);
        Lecturer lect24 = new Lecturer(24L, "Prof. Jason Allen", mechDept);
        Lecturer lect25 = new Lecturer(25L, "Dr. Karen Young", elecDept);

        // === COURSES ===
        // General Courses
        Course dld211 = new Course(1L, "DLD211", "Leadership and Development Studies", 2, null, 1000);
        Course tmc411 = new Course(2L, "TMC411", "Total Man Concept", 2, null, 1000);
        Course gst111 = new Course(3L, "GST111", "Communication in English", 2, null, 1000);
        Course gst211 = new Course(4L, "GST211", "Environment and Sustainable Development", 2, null, 1000);

        // Computer Science Courses
        Course csc111 = new Course(5L, "CSC111", "Introduction to Computer Science", 3, computerScience, 150);
        Course csc211 = new Course(6L, "CSC211", "Programming Fundamentals", 3, computerScience, 150);
        Course csc311 = new Course(7L, "CSC311", "Data Structures and Algorithms", 3, computerScience, 120);
        Course csc411 = new Course(8L, "CSC411", "Software Engineering", 3, computerScience, 100);
        Course csc421 = new Course(9L, "CSC421", "Computer Security", 3, computerScience, 80);
        Course csc431 = new Course(10L, "CSC431", "Computer Architecture", 3, computerScience, 90);

        // Information Technology Courses
        Course cit111 = new Course(11L, "CIT111", "Introduction to IT", 3, informationTech, 120);
        Course cit211 = new Course(12L, "CIT211", "Database Systems", 3, informationTech, 100);
        Course cit311 = new Course(13L, "CIT311", "Network Administration", 3, informationTech, 80);
        Course cit411 = new Course(14L, "CIT411", "System Analysis and Design", 3, informationTech, 70);

        // Architecture Courses
        Course arc111 = new Course(15L, "ARC111", "Architectural Design I", 4, architecture, 60);
        Course arc211 = new Course(16L, "ARC211", "Architectural Design II", 4, architecture, 55);
        Course arc311 = new Course(17L, "ARC311", "Architectural Design III", 4, architecture, 50);
        Course arc411 = new Course(18L, "ARC411", "Architectural Design IV", 4, architecture, 45);

        // Estate Management Courses
        Course esm111 = new Course(19L, "ESM111", "Introduction to Estate Management", 3, estateManagement, 80);
        Course esm211 = new Course(20L, "ESM211", "Property Law", 3, estateManagement, 75);
        Course esm311 = new Course(21L, "ESM311", "Property Valuation", 3, estateManagement, 70);
        Course esm411 = new Course(22L, "ESM411", "Real Estate Finance", 3, estateManagement, 65);

        // Mathematics Courses
        Course mth111 = new Course(23L, "MTH111", "Elementary Mathematics I", 3, mathematics, 100);
        Course mth211 = new Course(24L, "MTH211", "Elementary Mathematics II", 3, mathematics, 90);
        Course mth311 = new Course(25L, "MTH311", "Abstract Algebra", 3, mathematics, 60);
        Course mth411 = new Course(26L, "MTH411", "Real Analysis", 3, mathematics, 50);

        // Physics Courses
        Course phy111 = new Course(27L, "PHY111", "General Physics I", 3, physics, 120);
        Course phy211 = new Course(28L, "PHY211", "General Physics II", 3, physics, 110);
        Course phy311 = new Course(29L, "PHY311", "Modern Physics", 3, physics, 80);
        Course phy411 = new Course(30L, "PHY411", "Quantum Mechanics", 3, physics, 60);

        // Chemistry Courses
        Course chm111 = new Course(31L, "CHM111", "General Chemistry I", 3, chemistry, 150);
        Course chm211 = new Course(32L, "CHM211", "General Chemistry II", 3, chemistry, 140);
        Course chm311 = new Course(33L, "CHM311", "Organic Chemistry", 3, chemistry, 100);
        Course chm411 = new Course(34L, "CHM411", "Physical Chemistry", 3, chemistry, 80);

        // Accounting Courses
        Course acc111 = new Course(35L, "ACC111", "Introduction to Accounting", 3, accounting, 200);
        Course acc211 = new Course(36L, "ACC211", "Financial Accounting", 3, accounting, 180);
        Course acc311 = new Course(37L, "ACC311", "Cost Accounting", 3, accounting, 150);
        Course acc411 = new Course(38L, "ACC411", "Advanced Accounting", 3, accounting, 120);

        // Business Administration Courses
        Course bus111 = new Course(39L, "BUS111", "Introduction to Business", 3, businessAdmin, 250);
        Course bus211 = new Course(40L, "BUS211", "Business Management", 3, businessAdmin, 200);
        Course bus311 = new Course(41L, "BUS311", "Strategic Management", 3, businessAdmin, 150);
        Course bus411 = new Course(42L, "BUS411", "International Business", 3, businessAdmin, 100);

        // Economics Courses
        Course eco111 = new Course(43L, "ECO111", "Principles of Economics I", 3, economics, 180);
        Course eco211 = new Course(44L, "ECO211", "Principles of Economics II", 3, economics, 160);
        Course eco311 = new Course(45L, "ECO311", "Macroeconomics", 3, economics, 120);
        Course eco411 = new Course(46L, "ECO411", "Development Economics", 3, economics, 100);

        // Engineering Courses
        Course civ111 = new Course(47L, "CIV111", "Engineering Drawing", 3, civilEng, 80);
        Course civ211 = new Course(48L, "CIV211", "Structural Analysis", 3, civilEng, 70);
        Course mec111 = new Course(49L, "MEC111", "Engineering Mechanics", 3, mechEng, 75);
        Course mec211 = new Course(50L, "MEC211", "Thermodynamics", 3, mechEng, 65);
        Course ele111 = new Course(51L, "ELE111", "Circuit Analysis", 3, elecEng, 70);
        Course ele211 = new Course(52L, "ELE211", "Electronics", 3, elecEng, 60);

        // === COURSE ASSIGNMENTS (120+ assignments) ===
        List<CourseAssignment> assignments = new ArrayList<>();

        // General Course Assignments (4 courses × 10 sections each = 40 assignments)
        for (int i = 1; i <= 10; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, clds, aldcDept, null, dld211, List.of(lect21, lect22), cldsBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, clds, aldcDept, null, tmc411, List.of(lect21), cldsBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, clds, aldcDept, null, gst111, List.of(lect22), cldsBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, clds, aldcDept, null, gst211, List.of(lect21), cldsBuilding));
        }

        // Computer Science Assignments (6 courses × 3 sections each = 18 assignments)
        for (int i = 1; i <= 3; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc111, List.of(lect1), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc211, List.of(lect2), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc311, List.of(lect3), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc411, List.of(lect4), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc421, List.of(lect5), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, computerScience, csc431, List.of(lect1, lect2), cstBuilding));
        }

        // Information Technology Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, informationTech, cit111, List.of(lect3), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, informationTech, cit211, List.of(lect4), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, informationTech, cit311, List.of(lect5), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, cisDept, informationTech, cit411, List.of(lect1), cstBuilding));
        }

        // Architecture Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, archDept, architecture, arc111, List.of(lect6), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, archDept, architecture, arc211, List.of(lect7), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, archDept, architecture, arc311, List.of(lect8), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, archDept, architecture, arc411, List.of(lect6, lect7), cstBuilding));
        }

        // Estate Management Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, emDept, estateManagement, esm111, List.of(lect9), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, emDept, estateManagement, esm211, List.of(lect10), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, emDept, estateManagement, esm311, List.of(lect9), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, emDept, estateManagement, esm411, List.of(lect10), cstBuilding));
        }

        // Mathematics Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, mathDept, mathematics, mth111, List.of(lect11), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, mathDept, mathematics, mth211, List.of(lect12), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, mathDept, mathematics, mth311, List.of(lect11), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, mathDept, mathematics, mth411, List.of(lect12), cstBuilding));
        }

        // Physics Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, physicsDept, physics, phy111, List.of(lect13), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, physicsDept, physics, phy211, List.of(lect14), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, physicsDept, physics, phy311, List.of(lect13), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, physicsDept, physics, phy411, List.of(lect14), cstBuilding));
        }

        // Chemistry Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, chemDept, chemistry, chm111, List.of(lect15), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, chemDept, chemistry, chm211, List.of(lect16), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, chemDept, chemistry, chm311, List.of(lect15), cstBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cst, chemDept, chemistry, chm411, List.of(lect16), cstBuilding));
        }

        // Accounting Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, accDept, accounting, acc111, List.of(lect17), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, accDept, accounting, acc211, List.of(lect17), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, accDept, accounting, acc311, List.of(lect17), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, accDept, accounting, acc411, List.of(lect17), cmssBuilding));
        }

        // Business Administration Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, busDept, businessAdmin, bus111, List.of(lect18), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, busDept, businessAdmin, bus211, List.of(lect18), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, busDept, businessAdmin, bus311, List.of(lect18), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, busDept, businessAdmin, bus411, List.of(lect18), cmssBuilding));
        }

        // Economics Assignments (4 courses × 2 sections each = 8 assignments)
        for (int i = 1; i <= 2; i++) {
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, econDept, economics, eco111, List.of(lect19), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, econDept, economics, eco211, List.of(lect19), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, econDept, economics, eco311, List.of(lect19), cmssBuilding));
            assignments.add(new CourseAssignment((long) assignments.size() + 1, cmss, econDept, economics, eco411, List.of(lect19), cmssBuilding));
        }

        // Engineering Assignments (6 courses × 1 section each = 6 assignments)
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, civilDept, civilEng, civ111, List.of(lect23), coeBuilding));
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, civilDept, civilEng, civ211, List.of(lect23), coeBuilding));
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, mechDept, mechEng, mec111, List.of(lect24), coeBuilding));
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, mechDept, mechEng, mec211, List.of(lect24), coeBuilding));
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, elecDept, elecEng, ele111, List.of(lect25), coeBuilding));
        assignments.add(new CourseAssignment((long) assignments.size() + 1, coe, elecDept, elecEng, ele211, List.of(lect25), coeBuilding));

        return assignments;
    }
}
