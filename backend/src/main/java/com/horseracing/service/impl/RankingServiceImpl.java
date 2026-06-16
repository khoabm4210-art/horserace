package com.horseracing.service.impl;

import com.horseracing.dto.response.ranking.HorseRankingResponse;
import com.horseracing.dto.response.ranking.JockeyRankingResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.HorseRanking;
import com.horseracing.entity.JockeyRanking;
import com.horseracing.entity.Horse;
import com.horseracing.entity.Jockey;
import com.horseracing.repository.HorseRankingRepository;
import com.horseracing.repository.JockeyRankingRepository;
import com.horseracing.repository.HorseRepository;
import com.horseracing.repository.JockeyRepository;
import com.horseracing.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RankingServiceImpl implements RankingService {
    @Autowired
    private HorseRankingRepository horseRankingRepository;
    @Autowired
    private JockeyRankingRepository jockeyRankingRepository;
    @Autowired
    private HorseRepository horseRepository;
    @Autowired
    private JockeyRepository jockeyRepository;

    @Override
    public void upsertHorseRanking(Long seasonId, Long horseId, int points, boolean isWin) {
        Optional<HorseRanking> existing = horseRankingRepository.findBySeasonIdAndHorseId(seasonId, horseId);
        
        if (existing.isPresent()) {
            HorseRanking ranking = existing.get();
            ranking.setTotalPoints(ranking.getTotalPoints() + points);
            ranking.setTotalRaces(ranking.getTotalRaces() + 1);
            if (isWin) {
                ranking.setTotalWins(ranking.getTotalWins() + 1);
            }
            ranking.setUpdatedAt(LocalDateTime.now());
            horseRankingRepository.save(ranking);
        } else {
            Horse horse = horseRepository.findById(horseId).orElse(null);
            HorseRanking ranking = HorseRanking.builder()
                .seasonId(seasonId)
                .horse(horse)
                .totalPoints(points)
                .totalRaces(1)
                .totalWins(isWin ? 1 : 0)
                .updatedAt(LocalDateTime.now())
                .build();
            horseRankingRepository.save(ranking);
        }
    }

    @Override
    public void upsertJockeyRanking(Long seasonId, Long jockeyId, int points, boolean isWin) {
        Optional<JockeyRanking> existing = jockeyRankingRepository.findBySeasonIdAndJockeyId(seasonId, jockeyId);
        
        if (existing.isPresent()) {
            JockeyRanking ranking = existing.get();
            ranking.setTotalPoints(ranking.getTotalPoints() + points);
            ranking.setTotalRaces(ranking.getTotalRaces() + 1);
            if (isWin) {
                ranking.setTotalWins(ranking.getTotalWins() + 1);
            }
            ranking.setUpdatedAt(LocalDateTime.now());
            jockeyRankingRepository.save(ranking);
        } else {
            Jockey jockey = jockeyRepository.findById(jockeyId).orElse(null);
            JockeyRanking ranking = JockeyRanking.builder()
                .seasonId(seasonId)
                .jockey(jockey)
                .totalPoints(points)
                .totalRaces(1)
                .totalWins(isWin ? 1 : 0)
                .updatedAt(LocalDateTime.now())
                .build();
            jockeyRankingRepository.save(ranking);
        }
    }

    @Override
    public PageResponse<HorseRankingResponse> getHorseRankings(Long seasonId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("totalPoints").descending());
        Page<HorseRanking> rankings = horseRankingRepository.findBySeasonId(seasonId, pageable);
        
        return PageResponse.<HorseRankingResponse>builder()
            .content(rankings.getContent().stream()
                .map(r -> HorseRankingResponse.builder()
                    .id(r.getId())
                    .seasonId(r.getSeasonId())
                    .horseId(r.getHorse().getId())
                    .horseName(r.getHorse().getName())
                    .horseCode(r.getHorse().getCode())
                    .totalPoints(r.getTotalPoints())
                    .totalRaces(r.getTotalRaces())
                    .totalWins(r.getTotalWins())
                    .build())
                .collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(rankings.getTotalElements())
            .totalPages(rankings.getTotalPages())
            .last(rankings.isLast())
            .build();
    }

    @Override
    public PageResponse<JockeyRankingResponse> getJockeyRankings(Long seasonId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("totalPoints").descending());
        Page<JockeyRanking> rankings = jockeyRankingRepository.findBySeasonId(seasonId, pageable);
        
        return PageResponse.<JockeyRankingResponse>builder()
            .content(rankings.getContent().stream()
                .map(r -> JockeyRankingResponse.builder()
                    .id(r.getId())
                    .seasonId(r.getSeasonId())
                    .jockeyId(r.getJockey().getId())
                    .jockeyName(r.getJockey().getFullName())
                    .totalPoints(r.getTotalPoints())
                    .totalRaces(r.getTotalRaces())
                    .totalWins(r.getTotalWins())
                    .build())
                .collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(rankings.getTotalElements())
            .totalPages(rankings.getTotalPages())
            .last(rankings.isLast())
            .build();
    }
}
