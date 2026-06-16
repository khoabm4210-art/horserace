package com.horseracing.service;

import com.horseracing.dto.request.lane.LaneAssignRequest;
import com.horseracing.dto.response.lane.LaneResponse;
import java.util.List;

public interface LaneService {
    LaneResponse assignLane(LaneAssignRequest request, Long assignedBy);
    LaneResponse updateLane(Long id, LaneAssignRequest request, Long updatedBy);
    void deleteLane(Long id);
    LaneResponse getLane(Long id);
    List<LaneResponse> getLanesByRace(Long raceId);
}
