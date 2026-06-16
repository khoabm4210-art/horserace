package com.horseracing.dto.lane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaneResponse {
    private Long id;
    private Long raceId;
    private Long registrationId;
    private Integer laneNumber;
    private String horseName;
    private String jockeyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
