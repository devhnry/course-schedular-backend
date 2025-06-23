package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.College;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.CollegeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @Slf4j
@RequiredArgsConstructor
public class CollegeBuildingSeeder {

    private final CollegeBuildingRepository collegeBuildingRepository;
    private final CollegeRepository collegeRepository;

    public void seed() {
        if (collegeBuildingRepository.count() > 0) return;

        College CST = getCollege("CST");
        College CMSS = getCollege("CMSS");
        College CLDS = getCollege("CLDS");
        College CoE = getCollege("CoE");

        collegeBuildingRepository.saveAll(List.of(
                CollegeBuilding
                        .builder().code("CST").name("College of Science & Technology").college(CST).build(),
                CollegeBuilding
                        .builder().code("CMSS").name("College of Management & Social Sciences").college(CMSS).build(),
                CollegeBuilding
                        .builder().code("CLDS").name("College of Entrepreneurial Development Studies").college(CLDS).build(),
                CollegeBuilding
                        .builder().code("PETE").name("College of Petroleum & Petrochemical Engineering").college(CoE).build(),
                CollegeBuilding
                        .builder().code("MECH").name("College of Mechanical Engineering").college(CoE).build(),
                CollegeBuilding
                        .builder().code("CIVIL").name("College of Civil Engineering").college(CoE).build(),
                CollegeBuilding
                        .builder().code("EIE").name("College of Electrical Engineering").college(CoE).build()
        ));

        log.info("✅ Seeded College Buildings");
    }

    private College getCollege(String code) {
        return collegeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("College not found: " + code));
    }
}
