package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component @Slf4j
@RequiredArgsConstructor
public class DepartmentSeeder {

    private final DepartmentRepository departmentRepository;
    private final CollegeBuildingRepository buildingRepository;

    public void seed() {
        if (departmentRepository.count() > 0) return;

        CollegeBuilding CST  = getBuilding("CST");
        CollegeBuilding CMSS = getBuilding("CMSS");
        CollegeBuilding CLDS  = getBuilding("CLDS");

        CollegeBuilding CoE_PETE = getBuilding("PETE");
        CollegeBuilding CoE_MECH = getBuilding("MECH");
        CollegeBuilding CoE_CIVIL = getBuilding("CIVIL");
        CollegeBuilding CoE_EIE = getBuilding("EIE");

        List<Department> all = new ArrayList<>();

        // CST Departments
        all.addAll(List.of(
                new Department(null, "Architecture",                    "ARC", CST),
                new Department(null, "Building Technology",             "BLD",   CST),
                new Department(null, "Estate Management",               "ESM",   CST),
                new Department(null, "Biological Sciences",             "BIO",  CST),
                new Department(null, "Biochemistry",                    "BCH",  CST),
                new Department(null, "Chemistry",                       "CHM", CST),
                new Department(null, "Computer & Information Sciences", "CIS",  CST),
                new Department(null, "Mathematics",                     "MAT", CST),
                new Department(null, "Physics",                         "PHY", CST)
        ));

        // CMSS Departments
        all.addAll(List.of(
                new Department(null, "Accounting",              "ACC", CMSS),
                new Department(null, "Banking & Finance",       "BFN",   CMSS),
                new Department(null, "Business Management",     "BUS",   CMSS),
                new Department(null, "Economics",               "ECO", CMSS),
                new Department(null, "Mass Communication",      "MAC", CMSS),
                new Department(null, "Sociology",               "SOC",  CMSS)
        ));

        // Leadership & Development Studies (under CLDS)
        all.addAll(List.of(
                new Department(null, "Political Science & IR",         "PSI", CLDS),
                new Department(null, "Psychology",                     "PSY",  CLDS),
                new Department(null, "Languages & General Studies",    "LNG",  CLDS),
                new Department(null, "Leadership Studies",             "LDS",   CLDS),
                new Department(null, "ALDC",                           "ALDC", CLDS)
        ));

        // Colleges of Engineering (each under their own building)
        all.addAll(List.of(
                new Department(null, "Civil Engineering",              "CVE", CoE_CIVIL),
                new Department(null, "Electrical & Info Eng.",         "EIE", CoE_EIE),
                new Department(null, "Mechanical Engineering",         "MCE", CoE_MECH),
                new Department(null, "Petroleum Engineering",          "PET", CoE_PETE),
                new Department(null, "Chemical Engineering",           "CHE", CoE_PETE)
        ));

        departmentRepository.saveAll(all);
        log.info("✅ Seeded Departments across College Buildings.");
    }

    private CollegeBuilding getBuilding(String code) {
        return buildingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + code));
    }

}
