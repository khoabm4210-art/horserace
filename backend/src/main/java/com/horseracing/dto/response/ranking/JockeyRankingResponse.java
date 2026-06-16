package com.horseracing.dto.response.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JockeyRankingResponse {
    private Long id;
    private Long seasonId;
    private Long jockeyId;
    private String jockeyName;
    private Integer totalPoints;
    private Integer totalRaces;
    private Integer totalWins;
}
