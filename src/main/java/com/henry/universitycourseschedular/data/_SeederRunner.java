package com.henry.universitycourseschedular.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class _SeederRunner implements CommandLineRunner {

    private final CollegeSeeder collegeSeeder;
    private final CollegeBuildingSeeder collegeBuildingSeeder;
    private final DepartmentSeeder departmentSeeder;
    private final ProgramSeeder programSeeder;
    private final VenueSeeder venueSeeder;
    private final TimeSlotSeeder timeSlotSeeder;

    @Override
    public void run(String... args) {
        try {
            log.info("🌱 Starting database seeding process...");

            collegeSeeder.seed();
            collegeBuildingSeeder.seed();
            departmentSeeder.seed();
            programSeeder.seed();
            venueSeeder.seed();
            timeSlotSeeder.seed();

            log.info("✅ Database seeding completed successfully!");

        } catch (Exception e) {
            log.error("❌ Database seeding failed", e);
            throw new RuntimeException("Seeding process failed", e);
        }
    }
}
