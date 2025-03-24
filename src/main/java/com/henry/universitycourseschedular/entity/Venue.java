package com.henry.universitycourseschedular.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    private String venueNumber;

    private int estimatedCapacity;

    // Many venues can have many available timeslots.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "venue_timeslots",
            joinColumns = @JoinColumn(name = "venue_id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id"))
    private Set<TimeSlot> availableSlots;
}