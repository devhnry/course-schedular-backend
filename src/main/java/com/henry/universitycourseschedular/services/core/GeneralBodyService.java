package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.GeneralBodyDto;
import com.henry.universitycourseschedular.models.core.GeneralBody;

import java.util.List;

public interface GeneralBodyService {
    DefaultApiResponse<GeneralBody> createGeneralBody(GeneralBodyDto dto);
    DefaultApiResponse<GeneralBody> updateGeneralBody(Long id, GeneralBodyDto dto);
    DefaultApiResponse<?> deleteGeneralBody(Long id);
    DefaultApiResponse<List<GeneralBody>> getAllGeneralBodies();
    DefaultApiResponse<GeneralBody> getGeneralBodyById(Long id);
}
