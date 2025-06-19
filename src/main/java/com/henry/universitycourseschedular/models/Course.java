package com.henry.universitycourseschedular.models;

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
    private String code;

    @Column(nullable = false)
    private String title;

    private int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_name", referencedColumnName = "name")
    private Program program;

    private Integer expectedStudents;

    public boolean isGeneralCourse() {
        if (code == null) return false;
        String prefix = code.toUpperCase();
        return prefix.startsWith("TMC") || prefix.startsWith("DLD")
                || prefix.startsWith("GST") || prefix.startsWith("EDS");
    }

    public boolean isSportsCourse() {
        return code != null &&
                code.toUpperCase().startsWith("TMC") &&
                code.endsWith("2");
    }

    public Integer extractLevel() {
        if (code == null || code.length() < 6) return null;
        String upperCode = code.toUpperCase();

        // 🚫 Skip level extraction for DLD courses
        if (upperCode.startsWith("DLD")) return null;

        Pattern pattern = Pattern.compile("(\\d{3})$");
        Matcher matcher = pattern.matcher(upperCode);

        if (matcher.find()) {
            String digits = matcher.group(1);
            int firstDigit = Character.getNumericValue(digits.charAt(0));
            return firstDigit * 100;
        }
        return null;
    }
}
