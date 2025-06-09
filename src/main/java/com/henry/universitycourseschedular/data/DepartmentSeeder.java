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
                new Department(null, "ARCH", "Architecture",                       cst),
                new Department(null, "BT",   "Building Technology",               cst),
                new Department(null, "EM",   "Estate Management",                 cst),
                new Department(null, "BIS",  "Biological Sciences",               cst),
                new Department(null, "BCH",  "Biochemistry",                      cst),
                new Department(null, "CHEM", "Chemistry",                         cst),
                new Department(null, "CIS",  "Computer & Information Sciences",   cst),
                new Department(null, "MATH", "Mathematics",                       cst),
                new Department(null, "PHYS", "Physics",                           cst)
        ));

        // CMSS Departments
        all.addAll(List.of(
                new Department(null, "ACCT", "Accounting",                cmss),
                new Department(null, "BF",   "Banking & Finance",         cmss),
                new Department(null, "BM",   "Business Management",       cmss),
                new Department(null, "ECON", "Economics",                 cmss),
                new Department(null, "MCOM", "Mass Communication",        cmss),
                new Department(null, "SOC",  "Sociology",                 cmss)
        ));

        // Leadership & Development Studies (under CEDS)
        all.addAll(List.of(
                new Department(null, "PSIR", "Political Science & IR",    lds),
                new Department(null, "PSY",  "Psychology",                lds),
                new Department(null, "LGS",  "Languages & General Studies", lds),
                new Department(null, "LS",   "Leadership Studies",        lds)
        ));

        // Colleges of Engineering (each under their own building)
        all.addAll(List.of(
                new Department(null, "CIVIL",    "Civil Engineering",      coe3),
                new Department(null, "EIE",      "Electrical & Info Eng.", coe4),
                new Department(null, "MECH",     "Mechanical Engineering", coe2),
                new Department(null, "PETE",     "Petroleum Engineering",  coe1),
                new Department(null, "CHEMENG",  "Chemical Engineering",   coe1)  // or its own building
        ));

        departmentRepository.saveAll(all);
    }

    private CollegeBuilding getBuilding(String code) {
        return buildingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + code));
    }

}
