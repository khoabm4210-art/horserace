package com.horseracing.service.impl;

import com.horseracing.dto.response.dashboard.DashboardStatsResponse;
import com.horseracing.dto.response.race.RaceResponse;
import com.horseracing.dto.response.result.ResultResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.Race;
import com.horseracing.entity.RaceResult;
import com.horseracing.enums.RaceStatus;
import com.horseracing.enums.RegistrationStatus;
import com.horseracing.repository.*;
import com.horseracing.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HorseRepository horseRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private RaceResultRepository raceResultRepository;
    @Autowired
    private RaceRegistrationRepository registrationRepository;

    @Override
    public DashboardStatsResponse getStatsForAdmin() {
        long totalUsers = userRepository.count();
        long totalHorses = horseRepository.count();
        long totalRaces = raceRepository.count();
        long completedRaces = raceRepository.countByStatus(RaceStatus.COMPLETED);

        return DashboardStatsResponse.builder()
            .totalUsers(totalUsers)
            .totalHorses(totalHorses)
            .totalRaces(totalRaces)
            .completedRaces(completedRaces)
            .pendingRaces(totalRaces - completedRaces)
            .build();
    }

    @Override
    public DashboardStatsResponse getStatsForOrganizer() {
        long openRaces = raceRepository.countByStatus(RaceStatus.OPEN);
        long ongoingRaces = raceRepository.countByStatus(RaceStatus.ONGOING);
        long pendingRegistrations = registrationRepository.countByStatus(RegistrationStatus.PENDING);
        long approvedRegistrations = registrationRepository.countByStatus(RegistrationStatus.APPROVED);

        return DashboardStatsResponse.builder()
            .openRaces(openRaces)
            .ongoingRaces(ongoingRaces)
            .pendingRegistrations(pendingRegistrations)
            .approvedRegistrations(approvedRegistrations)
            .build();
    }

    @Override
    public DashboardStatsResponse getStatsForHorseOwner(Long ownerId) {
        long myHorses = horseRepository.countByOwnerId(ownerId);
        long pendingRegistrations = registrationRepository.countByOwnerAndStatus(ownerId, RegistrationStatus.PENDING);
        long approvedRegistrations = registrationRepository.countByOwnerAndStatus(ownerId, RegistrationStatus.APPROVED);

        return DashboardStatsResponse.builder()
            .myHorses(myHorses)
            .pendingRegistrations(pendingRegistrations)
            .approvedRegistrations(approvedRegistrations)
            .build();
    }

    @Override
    public PageResponse<RaceResponse> getUpcomingRaces(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("raceDate").ascending());
        Page<Race> races = raceRepository.findByRaceDateAfterAndStatusOrderByRaceDateAsc(LocalDateTime.now(), RaceStatus.OPEN, pageable);
        
        return PageResponse.<RaceResponse>builder()
            .content(races.getContent().stream()
                .map(this::mapToRaceResponse)
                .collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(races.getTotalElements())
            .totalPages(races.getTotalPages())
            .last(races.isLast())
            .build();
    }

    @Override
    public PageResponse<ResultResponse> getRecentResults(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<RaceResult> results = raceResultRepository.findByIsPublished(1, pageable);
        
        return PageResponse.<ResultResponse>builder()
            .content(results.getContent().stream()
                .map(r -> ResultResponse.builder()
                    .id(r.getId())
                    .raceId(r.getRace().getId())
                    .raceName(r.getRace().getName())
                    .isPublished(true)
                    .publishedAt(r.getPublishedAt().toString())
                    .build())
                .collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(results.getTotalElements())
            .totalPages(results.getTotalPages())
            .last(results.isLast())
            .build();
    }

    private RaceResponse mapToRaceResponse(Race race) {
        return RaceResponse.builder()
            .id(race.getId())
            .name(race.getName())
            .raceDate(race.getRaceDate().toString())
            .location(race.getLocation())
            .distance(race.getDistance())
            .maxHorses(race.getMaxHorses())
            .status(race.getStatus().name())
            .build();
    }
}
