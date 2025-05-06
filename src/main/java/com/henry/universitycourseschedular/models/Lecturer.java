package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Title;
import com.henry.universitycourseschedular.utils.IdUtils;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecturer{
    @Id
    private String lecturerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Title title;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @PrePersist
    public void ensureId() {
        if (this.lecturerId == null) {
            this.lecturerId = IdUtils.shortUUID();
        }
    }
}
