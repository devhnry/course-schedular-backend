package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Program;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component @Slf4j
@RequiredArgsConstructor
public class ProgramSeeder {

    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;

    public void seed() {
        if (programRepository.count() > 0) return;

        Function<String, Department> getDept = code ->
                departmentRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Dept not found: " + code));

        programRepository.saveAll(List.of(
                // ALDC is a program
                Program.builder().name("ALDC").department(getDept.apply("ALDC")).build(),

                // CST Programs
                Program.builder().name("Architecture").department(getDept.apply("ARC")).build(),
                Program.builder().name("Building Technology").department(getDept.apply("BLD")).build(),
                Program.builder().name("Estate Management").department(getDept.apply("ESM")).build(),
                Program.builder().name("Applied Biology & Biotechnology").department(getDept.apply("BIO")).build(),
                Program.builder().name("Microbiology").department(getDept.apply("BIO")).build(),
                Program.builder().name("Biochemistry").department(getDept.apply("BCH")).build(),
                Program.builder().name("Molecular Biology").department(getDept.apply("BIO")).build(),
                Program.builder().name("Industrial Chemistry").department(getDept.apply("CHM")).build(),
                Program.builder().name("Computer Science").department(getDept.apply("CIS")).build(),
                Program.builder().name("Management Information Systems").department(getDept.apply("CIS")).build(),
                Program.builder().name("Industrial Mathematics").department(getDept.apply("MAT")).build(),
                Program.builder().name("Industrial Physics").department(getDept.apply("PHY")).build(),

                // CMSS Programs
                Program.builder().name("Accounting").department(getDept.apply("ACC")).build(),
                Program.builder().name("Banking & Finance").department(getDept.apply("BFN")).build(),
                Program.builder().name("Business Administration").department(getDept.apply("BUS")).build(),
                Program.builder().name("Ind. Relations & HRM").department(getDept.apply("PSI")).build(),
                Program.builder().name("Marketing & Entrepreneurship").department(getDept.apply("BUS")).build(),
                Program.builder().name("Economics").department(getDept.apply("ECO")).build(),
                Program.builder().name("Demography & Social Stats").department(getDept.apply("ECO")).build(),
                Program.builder().name("Mass Communication").department(getDept.apply("MAC")).build(),
                Program.builder().name("Sociology").department(getDept.apply("SOC")).build(),

                // LDS Programs
                Program.builder().name("International Relations").department(getDept.apply("PSI")).build(),
                Program.builder().name("Policy & Strategic Studies").department(getDept.apply("PSI")).build(),
                Program.builder().name("Political Science").department(getDept.apply("PSI")).build(),
                Program.builder().name("English").department(getDept.apply("LNG")).build(),
                Program.builder().name("Psychology").department(getDept.apply("PSY")).build(),
                Program.builder().name("Leadership Certificate").department(getDept.apply("LDS")).build(),
                Program.builder().name("Leadership Diploma").department(getDept.apply("LDS")).build(),

                // CoE Programs
                Program.builder().name("Civil Engineering").department(getDept.apply("CVE")).build(),
                Program.builder().name("Computer Engineering").department(getDept.apply("EIE")).build(),
                Program.builder().name("Electrical & Electronics Eng.").department(getDept.apply("EIE")).build(),
                Program.builder().name("Inf. & Comm. Eng.").department(getDept.apply("EIE")).build(),
                Program.builder().name("Mechanical Engineering").department(getDept.apply("MCE")).build(),
                Program.builder().name("Petroleum Engineering").department(getDept.apply("PET")).build(),
                Program.builder().name("Chemical Engineering").department(getDept.apply("CHE")).build()
        ));

        log.info("✅ Seeded Programs across various departments");
    }
}
