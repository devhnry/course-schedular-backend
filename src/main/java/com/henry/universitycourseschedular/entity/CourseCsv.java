package com.henry.universitycourseschedular.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CourseCsv {
    @CsvBindByName(column = "Allocated Lecturer(s)")
    private String lecturer;

    @CsvBindByName(column = "Code")
    private String courseCode;

    @CsvBindByName(column = "Title")
    private String courseName;

    @CsvBindByName(column = "Home Department")
    private String department;

    @CsvBindByName(column = "Home Program")
    private String program;

    @CsvBindByName(column = "Level")
    private int level;

    @CsvBindByName(column = "Capacity")
    private int capacity; // This should take the amount of students that have registered for the course. With a -10
    // to +10 range

    @CsvBindByName(column = "Home College")
    private String collegeBuilding;
}

