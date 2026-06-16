package com.horseracing.dto.response.lane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaneResponse {
    private Long id;
    private Long raceId;
    private Long registrationId;
    private String horseName;
    private String jockeyName;
    private Integer laneNumber;
    private String assignedAt;
}
