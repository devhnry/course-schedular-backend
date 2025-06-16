package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.HodManagementDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HodManagementService {
    DefaultApiResponse<List<HodManagementDto>> listAllHods(Pageable pageable);
    DefaultApiResponse<HodManagementDto> updateHodAccess(String userId, Boolean grantWrite);
    DefaultApiResponse<?> deleteHod(String userId);
}
