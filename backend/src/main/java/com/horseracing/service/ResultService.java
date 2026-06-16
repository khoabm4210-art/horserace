package com.horseracing.service;

import com.horseracing.dto.request.result.ResultEntryRequest;
import com.horseracing.dto.response.result.ResultResponse;

public interface ResultService {
    ResultResponse createOrUpdateResult(Long raceId, ResultEntryRequest request);
    
    ResultResponse getResultByRaceId(Long raceId);
    
    ResultResponse publishResult(Long raceId);
}
