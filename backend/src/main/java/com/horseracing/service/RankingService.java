package com.horseracing.service;

import com.horseracing.dto.response.ranking.HorseRankingResponse;
import com.horseracing.dto.response.ranking.JockeyRankingResponse;
import com.horseracing.dto.response.PageResponse;

public interface RankingService {
    void upsertHorseRanking(Long seasonId, Long horseId, int points, boolean isWin);
    void upsertJockeyRanking(Long seasonId, Long jockeyId, int points, boolean isWin);
    PageResponse<HorseRankingResponse> getHorseRankings(Long seasonId, int page, int size);
    PageResponse<JockeyRankingResponse> getJockeyRankings(Long seasonId, int page, int size);
}
