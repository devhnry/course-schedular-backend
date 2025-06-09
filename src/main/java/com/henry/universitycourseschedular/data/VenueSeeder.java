package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.VenueMapper;
import com.henry.universitycourseschedular.models._dto.VenueDTO;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VenueSeeder {

    private final VenueRepository venueRepository;
    private final CollegeBuildingRepository buildingRepository;

    public void seed() {
        if (venueRepository.count() > 0) return;

        List<VenueDTO> dtos = List.of(
                // CMSS
                VenueDTO.builder().name("CMSS E101").capacity(192).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS A201").capacity(76).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS E201").capacity(69).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS G201").capacity(144).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS G301").capacity(100).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS B301").capacity(144).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS C301").capacity(488).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS E301").capacity(92).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS H301").capacity(132).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS E401A").capacity(36).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS E401B").capacity(0).collegeCode("CMSS").available(false).build(), // unavailable
                VenueDTO.builder().name("CMSS F301").capacity(92).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS F401").capacity(60).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS C401").capacity(128).collegeCode("CMSS").build(),
                VenueDTO.builder().name("CMSS H401").capacity(448).collegeCode("CMSS").build(),

                // CST
                VenueDTO.builder().name("CST H111 Lab").capacity(300).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H112").capacity(150).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H108").capacity(81).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H107").capacity(243).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Hall 204").capacity(120).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Hall 203").capacity(120).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Hall 202").capacity(110).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Hall 201").capacity(160).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Seminar Room (500L Only)").capacity(80).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Estate Lab").capacity(57).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Microbiology Lab").capacity(120).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Biology Lab").capacity(270).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Comp Lab 1").capacity(60).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Comp Lab 2").capacity(144).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H302").capacity(330).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Biochem Lab 1").capacity(100).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Biochem Lab 2").capacity(100).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H308").capacity(129).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H307").capacity(117).collegeCode("CST").build(),
                VenueDTO.builder().name("CST H306").capacity(120).collegeCode("CST").build(),
                VenueDTO.builder().name("CST MSc Studio 1").capacity(40).collegeCode("CST").build(),
                VenueDTO.builder().name("CST MSc Studio 2").capacity(40).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Digital Design Studio").capacity(60).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Studio 100").capacity(50).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Studio 200").capacity(50).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Studio 300").capacity(50).collegeCode("CST").build(),
                VenueDTO.builder().name("CST Studio 400").capacity(50).collegeCode("CST").build(),

                // General
                VenueDTO.builder().name("Lecture Theatre 1").capacity(2100).build(),
                VenueDTO.builder().name("Lecture Theatre 2").capacity(1100).build(),
                VenueDTO.builder().name("University Chapel").capacity(2500).build()
        );


        List<Venue> venues = dtos.stream()
                .filter(Objects::nonNull)
                .map(dto -> {
                    CollegeBuilding cb = buildingRepository.findByCode(dto.getCollegeCode())
                            .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + dto.getCollegeCode()));
                    return VenueMapper.toEntity(dto, cb);
                })
                .toList();

        venueRepository.saveAll(venues);
    }
}
