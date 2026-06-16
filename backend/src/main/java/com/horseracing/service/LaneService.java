package com.horseracing.service;

import com.horseracing.dto.lane.LaneCreateRequest;
import com.horseracing.dto.lane.LaneResponse;
import com.horseracing.entity.Lane;
import com.horseracing.entity.RaceRegistration;
import com.horseracing.enums.RegistrationStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LaneService {

    @Autowired
    private LaneRepository laneRepository;

    @Autowired
    private RaceRegistrationRepository registrationRepository;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private JockeyRepository jockeyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public LaneResponse assignLane(LaneCreateRequest request) {
        log.info("Assigning lane: {}", request.getLaneNumber());

        RaceRegistration registration = registrationRepository.findById(request.getRegistrationId())
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (!registration.getStatus().equals(RegistrationStatus.APPROVED)) {
            throw new BadRequestException("Only approved registrations can be assigned lanes");
        }

        // Check if lane already exists for this registration
        if (laneRepository.findByRegistration(request.getRegistrationId()).isPresent()) {
            throw new BadRequestException("This registration already has a lane assigned");
        }

        // Check if lane number is already taken in this race
        List<Lane> existingLanes = laneRepository.findByRace(registration.getRaceId());
        if (existingLanes.stream().anyMatch(l -> l.getLaneNumber().equals(request.getLaneNumber()))) {
            throw new BadRequestException("Lane " + request.getLaneNumber() + " is already taken");
        }

        Lane lane = Lane.builder()
            .raceId(registration.getRaceId())
            .registrationId(request.getRegistrationId())
            .laneNumber(request.getLaneNumber())
            .build();

        Lane savedLane = laneRepository.save(lane);
        log.info("Lane assigned with id: {}", savedLane.getId());

        return mapToResponse(savedLane);
    }

    @Transactional
    public LaneResponse updateLane(Long id, LaneCreateRequest request) {
        Lane lane = laneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lane not found"));

        // Check if new lane number is already taken
        List<Lane> existingLanes = laneRepository.findByRace(lane.getRaceId());
        if (existingLanes.stream()
            .filter(l -> !l.getId().equals(id))
            .anyMatch(l -> l.getLaneNumber().equals(request.getLaneNumber()))) {
            throw new BadRequestException("Lane " + request.getLaneNumber() + " is already taken");
        }

        lane.setLaneNumber(request.getLaneNumber());
        lane.setUpdatedAt(LocalDateTime.now());
        Lane updatedLane = laneRepository.save(lane);
        log.info("Lane updated: {}", id);

        return mapToResponse(updatedLane);
    }

    @Transactional
    public void deleteLane(Long id) {
        Lane lane = laneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lane not found"));

        lane.setDeleted(1);
        lane.setUpdatedAt(LocalDateTime.now());
        laneRepository.save(lane);
        log.info("Lane deleted: {}", id);
    }

    public List<LaneResponse> getLanesByRace(Long raceId) {
        return laneRepository.findByRace(raceId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    private LaneResponse mapToResponse(Lane lane) {
        LaneResponse response = modelMapper.map(lane, LaneResponse.class);

        registrationRepository.findById(lane.getRegistrationId()).ifPresent(reg -> {
            horseRepository.findById(reg.getHorseId()).ifPresent(horse ->
                response.setHorseName(horse.getName())
            );
            jockeyRepository.findById(reg.getJockeyId()).ifPresent(jockey ->
                response.setJockeyName(jockey.getName())
            );
        });

        return response;
    }
}
