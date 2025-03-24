package com.henry.universitycourseschedular.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CourseCsv {
    @CsvBindByName(column = "Lecturer")
    private String lecturer;

    @CsvBindByName(column = "CourseCode")
    private String courseCode;

    @CsvBindByName(column = "CourseName")
    private String courseName;

    @CsvBindByName(column = "Department")
    private String department;

    @CsvBindByName(column = "Level")
    private int level;

    @CsvBindByName(column = "Capacity")
    private int capacity;

    @CsvBindByName(column = "CollegeBuilding")
    private String collegeBuilding;
}

