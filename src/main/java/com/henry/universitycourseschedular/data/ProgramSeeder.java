package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Program;
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
                // CST Programs
                Program.builder().code("ARCH").name("Architecture").department(getDept.apply("ARCH")).build(),
                Program.builder().code("BT").name("Building Technology").department(getDept.apply("BT")).build(),
                Program.builder().code("EM").name("Estate Management").department(getDept.apply("EM")).build(),
                Program.builder().code("ABT").name("Applied Biology & Biotechnology").department(getDept.apply("BIS")).build(),
                Program.builder().code("MICR").name("Microbiology").department(getDept.apply("BIS")).build(),
                Program.builder().code("BIOCHEM").name("Biochemistry").department(getDept.apply("BCH")).build(),
                Program.builder().code("MBIO").name("Molecular Biology").department(getDept.apply("BCH")).build(),
                Program.builder().code("ICHEM").name("Industrial Chemistry").department(getDept.apply("CHEM")).build(),
                Program.builder().code("CS").name("Computer Science").department(getDept.apply("CIS")).build(),
                Program.builder().code("MIS").name("Management Info Systems").department(getDept.apply("CIS")).build(),
                Program.builder().code("IMATH").name("Industrial Mathematics").department(getDept.apply("MATH")).build(),
                Program.builder().code("IPHY").name("Industrial Physics").department(getDept.apply("PHYS")).build(),

                // CMSS Programs
                Program.builder().code("ACCT").name("Accounting").department(getDept.apply("ACCT")).build(),
                Program.builder().code("BF").name("Banking & Finance").department(getDept.apply("BF")).build(),
                Program.builder().code("BBA").name("Business Administration").department(getDept.apply("BM")).build(),
                Program.builder().code("IRHR").name("Ind. Relations & HRM").department(getDept.apply("BM")).build(),
                Program.builder().code("MKT").name("Marketing & Entrepreneurship").department(getDept.apply("BM")).build(),
                Program.builder().code("ECON").name("Economics").department(getDept.apply("ECON")).build(),
                Program.builder().code("DSS").name("Demography & Social Stats").department(getDept.apply("ECON")).build(),
                Program.builder().code("MCOM").name("Mass Communication").department(getDept.apply("MCOM")).build(),
                Program.builder().code("SOC").name("Sociology").department(getDept.apply("SOC")).build(),

                // LDS Programs
                Program.builder().code("IR").name("International Relations").department(getDept.apply("PSIR")).build(),
                Program.builder().code("PSS").name("Policy & Strategic Studies").department(getDept.apply("PSIR")).build(),
                Program.builder().code("PS").name("Political Science").department(getDept.apply("PSIR")).build(),
                Program.builder().code("ENG").name("English").department(getDept.apply("LGS")).build(),
                Program.builder().code("FRE").name("French").department(getDept.apply("LGS")).build(),
                Program.builder().code("PSY").name("Psychology").department(getDept.apply("PSY")).build(),
                Program.builder().code("CERTLEAD").name("Leadership Certificate").department(getDept.apply("LS")).build(),
                Program.builder().code("DIPLEAD").name("Leadership Diploma").department(getDept.apply("LS")).build(),

                // CoE Programs
                Program.builder().code("CIVIL").name("Civil Engineering").department(getDept.apply("CIVIL")).build(),
                Program.builder().code("CE").name("Computer Engineering").department(getDept.apply("EIE")).build(),
                Program.builder().code("EEE").name("Electrical & Electronics Eng.").department(getDept.apply("EIE")).build(),
                Program.builder().code("ICE").name("Inf. & Comm. Eng.").department(getDept.apply("EIE")).build(),
                Program.builder().code("MECH").name("Mechanical Engineering").department(getDept.apply("MECH")).build(),
                Program.builder().code("PETE").name("Petroleum Engineering").department(getDept.apply("PETE")).build(),
                Program.builder().code("CHEMENG").name("Chemical Engineering").department(getDept.apply("CHEMENG")).build()
        ));

        log.info("✅ Seeded Programs across various departments");
    }
}
