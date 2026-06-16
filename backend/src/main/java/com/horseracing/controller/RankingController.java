package com.horseracing.controller;

import com.horseracing.dto.response.ranking.HorseRankingResponse;
import com.horseracing.dto.response.ranking.JockeyRankingResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/rankings")
@Tag(name = "Rankings", description = "API bảng xếp hạng")
public class RankingController {
    @Autowired
    private RankingService rankingService;

    @GetMapping("/horses")
    @Operation(summary = "Lấy bảng xếp hạng ngựa")
    public ResponseEntity<ApiResponse<PageResponse<HorseRankingResponse>>> getHorseRankings(
            @RequestParam Long seasonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<HorseRankingResponse> response = rankingService.getHorseRankings(seasonId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/jockeys")
    @Operation(summary = "Lấy bảng xếp hạng nài")
    public ResponseEntity<ApiResponse<PageResponse<JockeyRankingResponse>>> getJockeyRankings(
            @RequestParam Long seasonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<JockeyRankingResponse> response = rankingService.getJockeyRankings(seasonId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
