//package com.henry.universitycourseschedular.data;
//
//import com.henry.universitycourseschedular.models.core.Department;
//import com.henry.universitycourseschedular.models.core.Venue;
//import com.henry.universitycourseschedular.models.schedule.VenueConstraint;
//import com.henry.universitycourseschedular.repositories.DepartmentRepository;
//import com.henry.universitycourseschedular.repositories.VenueConstraintRepository;
//import com.henry.universitycourseschedular.repositories.VenueRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component @RequiredArgsConstructor @Slf4j
//public class VenueConstraintSeeder {
//
//    private final VenueConstraintRepository venueConstraintRepository;
//    private final VenueRepository venueRepository;
//    private final DepartmentRepository departmentRepository;
//
//    public void seed() {
//        if (venueConstraintRepository.count() > 0) {
//            log.info("Venue constraints already seeded");
//            return;
//        }
//
//        log.info("Seeding venue constraints...");
//
//        List<Department> allDepartments = departmentRepository.findAllWithCollegeBuilding();
//        List<Venue> allVenues = venueRepository.findAllWithCollegeBuilding();
//
//        // Step 1: Department-based restriction
//        for (Venue venue : allVenues) {
//            if (venue.getCollegeBuilding() == null) {
//                log.info("Venue '{}' has no college building", venue.getName());
//                continue;
//            }
//
//            String buildingCode = venue.getCollegeBuilding().getCode();
//
//            List<Department> allowedDepartments = allDepartments.stream()
//                    .filter(dept -> dept.getCollegeBuilding().getCode().equals(buildingCode))
//                    .toList();
//
//            for (Department dept : allDepartments) {
//                if (!allowedDepartments.contains(dept)) {
//                    venueConstraintRepository.save(VenueConstraint.builder()
//                            .venue(venue)
//                            .preferredDepartment(dept)
//                            .restricted(true)
//                            .build());
//                }
//            }
//        }
//
//        // Step 2: General course overrides
//        log.info("Applying general course venue constraints...");
//
//        // Find a proxy department from a chapel venue
//        Venue chapelVenue = allVenues.stream()
//                .filter(v -> v.getName().toLowerCase().contains("chapel"))
//                .findFirst()
//                .orElse(null);
//
//        Venue lectureTheatreVenue = allVenues.stream()
//                .filter(v -> v.getName().toLowerCase().contains("lecture"))
//                .findFirst()
//                .orElse(null);
//
//        if (chapelVenue != null && lectureTheatreVenue != null) {
//            Department chapelDept = allDepartments.stream()
//                    .filter(d -> d.getCollegeBuilding().equals(chapelVenue.getCollegeBuilding()))
//                    .findFirst()
//                    .orElse(null);
//
//            Department theatreDept = allDepartments.stream()
//                    .filter(d -> d.getCollegeBuilding().equals(lectureTheatreVenue.getCollegeBuilding()))
//                    .findFirst()
//                    .orElse(null);
//
//            if (chapelDept != null) {
//                venueConstraintRepository.save(VenueConstraint.builder()
//                        .venue(chapelVenue)
//                        .preferredDepartment(chapelDept)
//                        .restricted(false)
//                        .build());
//
//                log.info("Allowed general courses like DLD/TMC to use '{}'", chapelVenue.getName());
//            }
//
//            if (theatreDept != null) {
//                venueConstraintRepository.save(VenueConstraint.builder()
//                        .venue(lectureTheatreVenue)
//                        .preferredDepartment(theatreDept)
//                        .restricted(false)
//                        .build());
//
//                log.info("Allowed general courses like GST/EDS to use '{}'", lectureTheatreVenue.getName());
//            }
//        } else {
//            log.warn("Could not find chapel or lecture theatre venue. General course constraints may fail.");
//        }
//
//        log.info("Venue constraints seeded successfully");
//    }
//
//}
