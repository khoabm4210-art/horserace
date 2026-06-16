package com.horseracing.service;

import com.horseracing.dto.request.season.SeasonCreateRequest;
import com.horseracing.dto.request.season.SeasonUpdateRequest;
import com.horseracing.dto.response.season.SeasonResponse;
import com.horseracing.dto.response.season.PointRuleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeasonService {
    Page<SeasonResponse> getAllSeasons(Pageable pageable, String status);
    
    SeasonResponse getSeasonById(Long id);
    
    SeasonResponse createSeason(SeasonCreateRequest request, Long createdBy);
    
    SeasonResponse updateSeason(Long id, SeasonUpdateRequest request);
    
    SeasonResponse closeSeason(Long id);
    
    List<PointRuleResponse> getPointRules(Long seasonId);
    
    void updatePointRules(Long seasonId, List<PointRuleResponse> rules);
}
