package com.henry.universitycourseschedular.models.course;

import com.henry.universitycourseschedular.models.core.GeneralBody;
import com.henry.universitycourseschedular.models.core.Program;
import jakarta.persistence.*;
import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    private int level;

    @Column(nullable = false)
    private int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    private GeneralBody generalBody;

    private Integer expectedStudents;

    public Integer getLevel() {
        if (courseCode == null || courseCode.length() < 4) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(courseCode);

        if (matcher.find()) {
            String numericPart = matcher.group();
            if (numericPart.length() >= 3) {
                int firstDigit = Character.getNumericValue(numericPart.charAt(0));
                return firstDigit * 100;
            }
        }
        return null;
    }

    public boolean isGeneral() {
        return generalBody != null;
    }

    public boolean isSportsCourse() {
        return courseCode != null &&
                courseCode.toUpperCase().startsWith("TMC") &&
                courseCode.endsWith("2");
    }
}
