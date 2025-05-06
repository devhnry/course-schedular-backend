package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.utils.IdUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String courseId;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private int units;

    @PrePersist
    public void ensureId() {
        if (this.courseId == null) {
            this.courseId = IdUtils.shortUUID();
        }
    }
}