package com.horseracing.service.impl;

import com.horseracing.dto.request.lane.LaneAssignRequest;
import com.horseracing.dto.response.lane.LaneResponse;
import com.horseracing.entity.Lane;
import com.horseracing.entity.RaceRegistration;
import com.horseracing.entity.Race;
import com.horseracing.entity.User;
import com.horseracing.enums.RegistrationStatus;
import com.horseracing.enums.RaceStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.LaneRepository;
import com.horseracing.repository.RaceRegistrationRepository;
import com.horseracing.repository.RaceRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.LaneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LaneServiceImpl implements LaneService {
    @Autowired
    private LaneRepository laneRepository;
    @Autowired
    private RaceRegistrationRepository registrationRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public LaneResponse assignLane(LaneAssignRequest request, Long assignedBy) {
        // Validate lane number
        if (request.getLaneNumber() < 1 || request.getLaneNumber() > 30) {
            throw new BadRequestException("Lane number must be between 1 and 30");
        }

        // Validate registration exists and is APPROVED
        RaceRegistration registration = registrationRepository.findById(request.getRegistrationId())
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        
        if (!registration.getStatus().equals(RegistrationStatus.APPROVED)) {
            throw new BadRequestException("Registration must be APPROVED to assign lane");
        }

        // Check lane uniqueness in race
        Long raceId = registration.getRace().getId();
        if (laneRepository.existsByRaceIdAndLaneNumber(raceId, request.getLaneNumber())) {
            throw new BadRequestException("Lane " + request.getLaneNumber() + " is already assigned in this race");
        }

        User assignedByUser = userRepository.findById(assignedBy)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Lane lane = Lane.builder()
            .race(registration.getRace())
            .registration(registration)
            .laneNumber(request.getLaneNumber())
            .assignedAt(LocalDateTime.now())
            .assignedBy(assignedByUser)
            .build();

        Lane saved = laneRepository.save(lane);
        log.info("Lane assigned: Lane {} for Registration {}", request.getLaneNumber(), request.getRegistrationId());
        
        return mapToLaneResponse(saved);
    }

    @Override
    public LaneResponse updateLane(Long id, LaneAssignRequest request, Long updatedBy) {
        Lane lane = laneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lane not found"));

        // Check if lane number is not used by another lane in same race
        if (!lane.getLaneNumber().equals(request.getLaneNumber())) {
            if (laneRepository.existsByRaceIdAndLaneNumber(lane.getRace().getId(), request.getLaneNumber())) {
                throw new BadRequestException("Lane " + request.getLaneNumber() + " is already assigned in this race");
            }
        }

        lane.setLaneNumber(request.getLaneNumber());
        Lane updated = laneRepository.save(lane);
        log.info("Lane updated: {}", id);
        
        return mapToLaneResponse(updated);
    }

    @Override
    public void deleteLane(Long id) {
        Lane lane = laneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lane not found"));

        // Can only delete if race is in OPEN or ONGOING status
        if (!lane.getRace().getStatus().equals(RaceStatus.OPEN) && 
            !lane.getRace().getStatus().equals(RaceStatus.ONGOING)) {
            throw new BadRequestException("Cannot delete lane after race is completed or cancelled");
        }

        laneRepository.delete(lane);
        log.info("Lane deleted: {}", id);
    }

    @Override
    public LaneResponse getLane(Long id) {
        Lane lane = laneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lane not found"));
        return mapToLaneResponse(lane);
    }

    @Override
    public List<LaneResponse> getLanesByRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Race not found"));
        
        List<Lane> lanes = laneRepository.findByRaceIdOrderByLaneNumber(raceId);
        return lanes.stream().map(this::mapToLaneResponse).collect(Collectors.toList());
    }

    private LaneResponse mapToLaneResponse(Lane lane) {
        return LaneResponse.builder()
            .id(lane.getId())
            .raceId(lane.getRace().getId())
            .registrationId(lane.getRegistration().getId())
            .horseName(lane.getRegistration().getHorse().getName())
            .jockeyName(lane.getRegistration().getJockey().getFullName())
            .laneNumber(lane.getLaneNumber())
            .assignedAt(lane.getAssignedAt().toString())
            .build();
    }
}
