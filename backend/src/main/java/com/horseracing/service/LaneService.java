package com.horseracing.service;

import com.horseracing.dto.request.lane.LaneAssignRequest;
import com.horseracing.dto.response.lane.LaneResponse;

public interface LaneService {
    LaneResponse assignLane(LaneAssignRequest request, Long assignedBy);
    
    LaneResponse updateLane(Long id, LaneAssignRequest request);
    
    void deleteLane(Long id);
    
    LaneResponse getLaneById(Long id);
}
