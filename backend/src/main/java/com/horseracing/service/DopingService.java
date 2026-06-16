package com.horseracing.service;

import com.horseracing.dto.response.health.DopingTestResponse;
import com.horseracing.enums.DopingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DopingService {
    Page<DopingTestResponse> getDopingTests(Pageable pageable, Long horseId, Long raceId);
    
    DopingTestResponse createDopingTest(Long horseId, Long raceId, DopingResult result, Long createdBy);
    
    DopingTestResponse getDopingTestById(Long id);
}
