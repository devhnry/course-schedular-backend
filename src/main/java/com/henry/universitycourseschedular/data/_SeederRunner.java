package com.henry.universitycourseschedular.data;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class _SeederRunner {

    private final CollegeBuildingSeeder collegeBuildingSeeder;
    private final DepartmentSeeder departmentSeeder;
    private final ProgramSeeder programSeeder;
    private final VenueSeeder venueSeeder;
    private final TimeSlotSeeder timeSlotSeeder;

    @PostConstruct
    public void run() {
        collegeBuildingSeeder.seed();
        departmentSeeder.seed();
        programSeeder.seed();
//        venueSeeder.seed();
        timeSlotSeeder.seed();
    }

}
