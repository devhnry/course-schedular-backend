package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentSeeder {

    private final DepartmentRepository departmentRepository;
    private final CollegeBuildingRepository buildingRepository;

    public void seed() {
        if (departmentRepository.count() > 0) return;

        CollegeBuilding cst  = getBuilding("CST");
        CollegeBuilding cmss = getBuilding("CMSS");
        CollegeBuilding lds  = getBuilding("CEDS"); // Leadership & Dev Studies under CEDS
        CollegeBuilding coe1 = getBuilding("PETE");
        CollegeBuilding coe2 = getBuilding("MECH");
        CollegeBuilding coe3 = getBuilding("CIVIL");
        CollegeBuilding coe4 = getBuilding("EIE");

        List<Department> all = new ArrayList<>();

        // CST Departments
        all.addAll(List.of(
                new Department(null, "Architecture",                    "ARCH", cst),
                new Department(null, "Building Technology",             "BT",   cst),
                new Department(null, "Estate Management",               "EM",   cst),
                new Department(null, "Biological Sciences",             "BIS",  cst),
                new Department(null, "Biochemistry",                    "BCH",  cst),
                new Department(null, "Chemistry",                       "CHEM", cst),
                new Department(null, "Computer & Information Sciences", "CIS",  cst),
                new Department(null, "Mathematics",                     "MATH", cst),
                new Department(null, "Physics",                         "PHYS", cst)
        ));

        // CMSS Departments
        all.addAll(List.of(
                new Department(null, "Accounting",              "ACCT", cmss),
                new Department(null, "Banking & Finance",       "BF",   cmss),
                new Department(null, "Business Management",     "BM",   cmss),
                new Department(null, "Economics",               "ECON", cmss),
                new Department(null, "Mass Communication",      "MCOM", cmss),
                new Department(null, "Sociology",               "SOC",  cmss)
        ));

        // Leadership & Development Studies (under CEDS)
        all.addAll(List.of(
                new Department(null, "Political Science & IR",         "PSIR", lds),
                new Department(null, "Psychology",                     "PSY",  lds),
                new Department(null, "Languages & General Studies",    "LGS",  lds),
                new Department(null, "Leadership Studies",             "LS",   lds)
        ));

        // Colleges of Engineering (each under their own building)
        all.addAll(List.of(
                new Department(null, "Civil Engineering",              "CIVIL",    coe3),
                new Department(null, "Electrical & Info Eng.",         "EIE",      coe4),
                new Department(null, "Mechanical Engineering",         "MECH",     coe2),
                new Department(null, "Petroleum Engineering",          "PETE",     coe1),
                new Department(null, "Chemical Engineering",           "CHEMENG",  coe1)
        ));

        departmentRepository.saveAll(all);
    }

    private CollegeBuilding getBuilding(String code) {
        return buildingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + code));
    }

}
