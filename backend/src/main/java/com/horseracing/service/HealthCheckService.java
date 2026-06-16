package com.horseracing.service;

import com.horseracing.dto.response.health.HealthRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HealthCheckService {
    Page<HealthRecordResponse> getHealthRecords(Pageable pageable, Long horseId, Long raceId);
    
    HealthRecordResponse createHealthRecord(Long horseId, String recordData, Long createdBy);
    
    HealthRecordResponse getHealthRecordById(Long id);
}
