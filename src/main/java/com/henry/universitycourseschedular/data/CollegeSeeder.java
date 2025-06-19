package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.models.College;
import com.henry.universitycourseschedular.repositories.CollegeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CollegeSeeder {

    private final CollegeRepository collegeRepository;

    public void seed(){
        if (collegeRepository.count() > 0) return;

        collegeRepository.saveAll(List.of(
                College.builder().code("CST").name("College of Science & Technology").build(),
                College.builder().code("CMSS").name("College of Management & Social Sciences").build(),
                College.builder().code("CLDS").name("College of Entrepreneurial Development Studies").build(),
                College.builder().code("CoE").name("College of Petroleum & Petrochemical Engineering").build()
        ));

        log.info("✅ Seeded Colleges");
    }
}