package com.horseracing.service;

import com.horseracing.dto.registration.RegistrationCreateRequest;
import com.horseracing.dto.registration.RegistrationResponse;
import com.horseracing.entity.Horse;
import com.horseracing.entity.Jockey;
import com.horseracing.entity.Race;
import com.horseracing.entity.RaceRegistration;
import com.horseracing.enums.HorseStatus;
import com.horseracing.enums.JockeyStatus;
import com.horseracing.enums.RaceStatus;
import com.horseracing.enums.RegistrationStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ForbiddenException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RegistrationService {

    @Autowired
    private RaceRegistrationRepository registrationRepository;

    @Autowired
    private RaceRepository raceRepository;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private JockeyRepository jockeyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public RegistrationResponse createRegistration(Long ownerId, RegistrationCreateRequest request) {
        log.info("Creating registration for owner: {}", ownerId);

        // Validate race
        Race race = raceRepository.findById(request.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found"));

        if (!race.getStatus().equals(RaceStatus.OPEN)) {
            throw new BadRequestException("Race is not open for registration");
        }

        // Validate horse
        Horse horse = horseRepository.findById(request.getHorseId())
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getStatus().equals(HorseStatus.APPROVED)) {
            throw new BadRequestException("Horse is not approved for racing");
        }

        if (!horse.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("This horse does not belong to you");
        }

        // Validate jockey
        Jockey jockey = jockeyRepository.findById(request.getJockeyId())
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found"));

        if (!jockey.getStatus().equals(JockeyStatus.APPROVED)) {
            throw new BadRequestException("Jockey is not approved for racing");
        }

        if (!jockey.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("This jockey does not belong to you");
        }

        // Check if already registered
        if (registrationRepository.findByRaceAndHorse(request.getRaceId(), request.getHorseId()).isPresent()) {
            throw new BadRequestException("This horse is already registered for this race");
        }

        // Check if race has available slots
        int approvedCount = registrationRepository.countApprovedByRace(request.getRaceId());
        if (approvedCount >= race.getMaxHorses()) {
            throw new BadRequestException("Race is full");
        }

        RaceRegistration registration = RaceRegistration.builder()
            .raceId(request.getRaceId())
            .horseId(request.getHorseId())
            .jockeyId(request.getJockeyId())
            .ownerId(ownerId)
            .status(RegistrationStatus.PENDING)
            .build();

        RaceRegistration savedRegistration = registrationRepository.save(registration);
        log.info("Registration created with id: {}", savedRegistration.getId());

        return mapToResponse(savedRegistration);
    }

    public Page<RegistrationResponse> getMyRegistrations(Long ownerId, Pageable pageable) {
        return registrationRepository.findByOwner(ownerId, pageable)
            .map(this::mapToResponse);
    }

    public Page<RegistrationResponse> getRegistrationsByRace(Long raceId, Pageable pageable) {
        return registrationRepository.findByRace(raceId, pageable)
            .map(this::mapToResponse);
    }

    public Page<RegistrationResponse> getRegistrationsByRaceAndStatus(Long raceId, RegistrationStatus status, Pageable pageable) {
        return registrationRepository.findByRaceAndStatus(raceId, status, pageable)
            .map(this::mapToResponse);
    }

    public RegistrationResponse getRegistrationById(Long id) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        return mapToResponse(registration);
    }

    @Transactional
    public RegistrationResponse approveRegistration(Long id) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (!registration.getStatus().equals(RegistrationStatus.PENDING)) {
            throw new BadRequestException("Only pending registrations can be approved");
        }

        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setUpdatedAt(LocalDateTime.now());
        RaceRegistration updatedRegistration = registrationRepository.save(registration);
        log.info("Registration approved: {}", id);

        return mapToResponse(updatedRegistration);
    }

    @Transactional
    public RegistrationResponse rejectRegistration(Long id, String reason) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (!registration.getStatus().equals(RegistrationStatus.PENDING)) {
            throw new BadRequestException("Only pending registrations can be rejected");
        }

        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registration.setUpdatedAt(LocalDateTime.now());
        RaceRegistration updatedRegistration = registrationRepository.save(registration);
        log.info("Registration rejected: {}", id);

        return mapToResponse(updatedRegistration);
    }

    private RegistrationResponse mapToResponse(RaceRegistration registration) {
        RegistrationResponse response = modelMapper.map(registration, RegistrationResponse.class);
        
        raceRepository.findById(registration.getRaceId()).ifPresent(race ->
            response.setRaceName(race.getName())
        );
        
        horseRepository.findById(registration.getHorseId()).ifPresent(horse ->
            response.setHorseName(horse.getName())
        );
        
        jockeyRepository.findById(registration.getJockeyId()).ifPresent(jockey ->
            response.setJockeyName(jockey.getName())
        );

        return response;
    }
}
