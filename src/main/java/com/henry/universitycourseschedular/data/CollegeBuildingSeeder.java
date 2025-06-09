package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CollegeBuildingSeeder {

    private final CollegeBuildingRepository collegeBuildingRepository;

    public void seed() {
        if (collegeBuildingRepository.count() > 0) return;

        collegeBuildingRepository.saveAll(List.of(
                CollegeBuilding.builder().code("CST").name("College of Science & Technology").build(),
                CollegeBuilding.builder().code("CMSS").name("College of Management & Social Sciences").build(),
                CollegeBuilding.builder().code("CEDS").name("College of Entrepreneurial Development Studies").build(),
                CollegeBuilding.builder().code("PETE").name("College of Petroleum & Petrochemical Engineering").build(),
                CollegeBuilding.builder().code("MECH").name("College of Mechanical Engineering").build(),
                CollegeBuilding.builder().code("CIVIL").name("College of Civil Engineering").build(),
                CollegeBuilding.builder().code("EIE").name("College of Electrical Engineering").build()
        ));
    }
}
