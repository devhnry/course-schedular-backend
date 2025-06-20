package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.VenueMapper;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models.Venue;
import com.henry.universitycourseschedular.models._dto.VenueSeedDto;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component @Slf4j
@RequiredArgsConstructor
public class VenueSeeder {

    private final VenueRepository venueRepository;
    private final CollegeBuildingRepository buildingRepository;
    private final VenueMapper venueMapper;

    public void seed() {
        if (venueRepository.count() > 0) return;

        List<VenueSeedDto> dtos = List.of(
                // CMSS Building Venues (Based on the HTML timetable)
                VenueSeedDto.builder().name("CDS A201").capacity(76).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS B301").capacity(144).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS C301").capacity(488).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS C401").capacity(128).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS E101").capacity(192).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS E201").capacity(69).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS E301").capacity(92).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS E401A").capacity(36).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS F301").capacity(92).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS F401").capacity(60).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS G201").capacity(144).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS G301").capacity(100).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS H301").capacity(132).collegeCode("CMSS").build(),
                VenueSeedDto.builder().name("CDS H401").capacity(448).collegeCode("CMSS").build(),

                // CEDS Building Venues
                VenueSeedDto.builder().name("CEDS MKT LAB").capacity(80).collegeCode("CMSS").build(),

                // CST Building Venues
                VenueSeedDto.builder().name("CST H111 Lab").capacity(300).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H112").capacity(150).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H108").capacity(81).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H107").capacity(243).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Hall 204").capacity(120).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Hall 203").capacity(120).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Hall 202").capacity(110).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Hall 201").capacity(160).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Seminar Room").capacity(80).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Estate Lab").capacity(57).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Microbiology Lab").capacity(120).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Biology Lab").capacity(270).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Comp Lab 1").capacity(60).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Comp Lab 2").capacity(144).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H302").capacity(330).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Biochem Lab 1").capacity(100).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Biochem Lab 2").capacity(100).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H308").capacity(129).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H307").capacity(117).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST H306").capacity(120).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST MSc Studio 1").capacity(40).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST MSc Studio 2").capacity(40).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Digital Design Studio").capacity(60).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Studio 100").capacity(50).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Studio 200").capacity(50).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Studio 300").capacity(50).collegeCode("CST").build(),
                VenueSeedDto.builder().name("CST Studio 400").capacity(50).collegeCode("CST").build(),

                // COE Building Venues
                VenueSeedDto.builder().name("CHE 300LH").capacity(200).collegeCode("COE").build(),
                VenueSeedDto.builder().name("COE Lab 1").capacity(80).collegeCode("COE").build(),
                VenueSeedDto.builder().name("COE Lab 2").capacity(80).collegeCode("COE").build(),
                VenueSeedDto.builder().name("COE Workshop").capacity(100).collegeCode("COE").build(),

                // CLDS Building Venues
                VenueSeedDto.builder().name("CLDS Hall 1").capacity(150).collegeCode("CLDS").build(),
                VenueSeedDto.builder().name("CLDS Hall 2").capacity(120).collegeCode("CLDS").build(),

                // General/Central Venues (No specific college building)
                VenueSeedDto.builder().name("University Chapel").capacity(2500).build(),
                VenueSeedDto.builder().name("Chapel").capacity(2500).build(), // Alternative name
                VenueSeedDto.builder().name("Lecture Theatre 1").capacity(2100).build(),
                VenueSeedDto.builder().name("Lecture Theatre 2").capacity(1100).build()
        );

        List<Venue> venues = dtos.stream()
                .filter(Objects::nonNull)
                .map(dto -> {
                    CollegeBuilding cb = null;
                    if (dto.getCollegeCode() != null) {
                        cb = buildingRepository.findByCode(dto.getCollegeCode())
                                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + dto.getCollegeCode()));
                    }
                    return venueMapper.toEntity(dto, cb);
                })
                .toList();

        venueRepository.saveAll(venues);

        log.info("✅ Seeded {} venues across different college buildings", venues.size());
    }
}
