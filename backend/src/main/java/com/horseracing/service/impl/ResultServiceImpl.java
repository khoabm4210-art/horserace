package com.horseracing.service.impl;

import com.horseracing.dto.request.result.ResultEntryRequest;
import com.horseracing.dto.response.result.ResultResponse;
import com.horseracing.entity.*;
import com.horseracing.enums.RaceStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.*;
import com.horseracing.service.ResultService;
import com.horseracing.service.RankingService;
import com.horseracing.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ResultServiceImpl implements ResultService {
    @Autowired
    private RaceResultRepository raceResultRepository;
    @Autowired
    private RaceResultDetailRepository resultDetailRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private RaceRegistrationRepository registrationRepository;
    @Autowired
    private PointRuleRepository pointRuleRepository;
    @Autowired
    private HorseRepository horseRepository;
    @Autowired
    private JockeyRepository jockeyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RankingService rankingService;
    @Autowired
    private NotificationService notificationService;

    @Override
    public ResultResponse entryResult(ResultEntryRequest request, Long enteredBy) {
        Race race = raceRepository.findById(request.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found"));

        // Check if result already exists
        if (raceResultRepository.findByRaceId(request.getRaceId()).isPresent()) {
            throw new BadRequestException("Result already exists for this race");
        }

        RaceResult result = RaceResult.builder()
            .race(race)
            .isPublished(0)
            .createdAt(LocalDateTime.now())
            .build();

        RaceResult savedResult = raceResultRepository.save(result);

        // Create result details
        for (ResultEntryRequest.ResultDetailItem item : request.getDetails()) {
            RaceRegistration registration = registrationRepository.findById(item.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

            RaceResultDetail detail = RaceResultDetail.builder()
                .result(savedResult)
                .registration(registration)
                .horseId(registration.getHorse().getId())
                .jockeyId(registration.getJockey().getId())
                .finishPosition(item.getFinishPosition())
                .finishTime(item.getFinishTime())
                .pointsEarned(0) // Will be calculated on publish
                .notes(item.getNotes())
                .build();

            resultDetailRepository.save(detail);
        }

        log.info("Result entered for race: {}", request.getRaceId());
        return mapToResultResponse(savedResult);
    }

    @Override
    public ResultResponse updateResult(Long raceId, ResultEntryRequest request, Long updatedBy) {
        RaceResult result = raceResultRepository.findByRaceId(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

        if (result.getIsPublished() == 1) {
            throw new BadRequestException("Cannot update published result");
        }

        // Delete existing details
        resultDetailRepository.deleteByResultId(result.getId());

        // Create new details
        for (ResultEntryRequest.ResultDetailItem item : request.getDetails()) {
            RaceRegistration registration = registrationRepository.findById(item.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

            RaceResultDetail detail = RaceResultDetail.builder()
                .result(result)
                .registration(registration)
                .horseId(registration.getHorse().getId())
                .jockeyId(registration.getJockey().getId())
                .finishPosition(item.getFinishPosition())
                .finishTime(item.getFinishTime())
                .pointsEarned(0)
                .notes(item.getNotes())
                .build();

            resultDetailRepository.save(detail);
        }

        log.info("Result updated for race: {}", raceId);
        return mapToResultResponse(result);
    }

    @Override
    public ResultResponse publishResult(Long raceId, Long publishedBy) {
        RaceResult result = raceResultRepository.findByRaceId(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

        if (result.getIsPublished() == 1) {
            throw new BadRequestException("Result is already published");
        }

        Race race = result.getRace();
        Long seasonId = race.getSeason().getId();

        // Get point rules for season
        List<PointRule> rules = pointRuleRepository.findBySeasonId(seasonId);
        Map<Integer, Integer> pointMap = new HashMap<>();
        for (PointRule rule : rules) {
            pointMap.put(rule.getPosition(), rule.getPoint());
        }

        // Fallback: position 99 = 1 point
        int fallbackPoint = pointMap.getOrDefault(99, 1);

        // Calculate points for each detail
        List<RaceResultDetail> details = resultDetailRepository.findByResultId(result.getId());
        for (RaceResultDetail detail : details) {
            int position = detail.getFinishPosition();
            int points = pointMap.getOrDefault(position, fallbackPoint);
            detail.setPointsEarned(points);
            resultDetailRepository.save(detail);

            // Upsert rankings
            boolean isWin = position == 1;
            rankingService.upsertHorseRanking(seasonId, detail.getHorseId(), points, isWin);
            rankingService.upsertJockeyRanking(seasonId, detail.getJockeyId(), points, isWin);
        }

        // Publish result
        result.setIsPublished(1);
        result.setPublishedAt(LocalDateTime.now());
        User publishedByUser = userRepository.findById(publishedBy)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        result.setPublishedBy(publishedByUser);

        RaceResult savedResult = raceResultRepository.save(result);

        // Update race status
        race.setStatus(RaceStatus.COMPLETED);
        raceRepository.save(race);

        // Send broadcast notification
        notificationService.broadcastResultNotification(
            "Kết quả cuộc đua",
            "Kết quả của cuộc đua " + race.getName() + " đã được công bố",
            "RESULT",
            result.getId()
        );

        log.info("Result published for race: {}", raceId);
        return mapToResultResponse(savedResult);
    }

    @Override
    public ResultResponse getResult(Long raceId) {
        RaceResult result = raceResultRepository.findByRaceId(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        return mapToResultResponse(result);
    }

    private ResultResponse mapToResultResponse(RaceResult result) {
        List<RaceResultDetail> details = resultDetailRepository.findByResultId(result.getId());
        List<ResultResponse.ResultDetailResponse> detailResponses = details.stream()
            .map(d -> ResultResponse.ResultDetailResponse.builder()
                .finishPosition(d.getFinishPosition())
                .horseId(d.getHorseId())
                .horseName(d.getRegistration().getHorse().getName())
                .horseCode(d.getRegistration().getHorse().getCode())
                .jockeyId(d.getJockeyId())
                .jockeyName(d.getRegistration().getJockey().getFullName())
                .laneNumber(d.getRegistration().getLane() != null ? d.getRegistration().getLane().getLaneNumber() : null)
                .finishTime(d.getFinishTime())
                .pointsEarned(d.getPointsEarned())
                .notes(d.getNotes())
                .build())
            .collect(Collectors.toList());

        return ResultResponse.builder()
            .id(result.getId())
            .raceId(result.getRace().getId())
            .raceName(result.getRace().getName())
            .isPublished(result.getIsPublished() == 1)
            .publishedAt(result.getPublishedAt() != null ? result.getPublishedAt().toString() : null)
            .details(detailResponses)
            .build();
    }
}
