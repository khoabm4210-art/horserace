package com.horseracing.dto.response.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorseRankingResponse {
    private Long id;
    private Long seasonId;
    private Long horseId;
    private String horseName;
    private String horseCode;
    private Integer totalPoints;
    private Integer totalRaces;
    private Integer totalWins;
}
