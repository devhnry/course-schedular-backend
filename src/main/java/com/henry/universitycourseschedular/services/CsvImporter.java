package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.entity.CourseCsv;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
public class CsvImporter {
    public List<CourseCsv> importCourses(String csvFilePath) throws Exception {
        FileReader reader = new FileReader(csvFilePath);
        return new CsvToBeanBuilder<CourseCsv>(reader)
                .withType(CourseCsv.class)
                .build()
                .parse();
    }
}
