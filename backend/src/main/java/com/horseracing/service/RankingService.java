package com.horseracing.service;

import com.horseracing.dto.response.ranking.HorseRankingResponse;
import com.horseracing.dto.response.ranking.JockeyRankingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RankingService {
    Page<HorseRankingResponse> getHorseRankings(Long seasonId, Pageable pageable);
    
    Page<JockeyRankingResponse> getJockeyRankings(Long seasonId, Pageable pageable);
    
    void updateHorseRanking(Long seasonId, Long horseId, int pointsEarned, boolean isWinner);
    
    void updateJockeyRanking(Long seasonId, Long jockeyId, int pointsEarned, boolean isWinner);
}
