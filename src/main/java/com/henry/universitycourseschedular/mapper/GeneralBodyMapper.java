package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.GeneralBodyDto;
import com.henry.universitycourseschedular.models.core.GeneralBody;

public class GeneralBodyMapper {
    public static GeneralBody fromDto(GeneralBodyDto dto) {
        return new GeneralBody(null, dto.name(), dto.code());
    }

    public static void updateFromDto(GeneralBody existing, GeneralBodyDto dto) {
        existing.setName(dto.name());
        existing.setCode(dto.code());
    }
}
