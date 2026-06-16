package com.horseracing.service;

import com.horseracing.dto.request.result.ResultEntryRequest;
import com.horseracing.dto.response.result.ResultResponse;

public interface ResultService {
    ResultResponse entryResult(ResultEntryRequest request, Long enteredBy);
    ResultResponse updateResult(Long raceId, ResultEntryRequest request, Long updatedBy);
    ResultResponse publishResult(Long raceId, Long publishedBy);
    ResultResponse getResult(Long raceId);
}
