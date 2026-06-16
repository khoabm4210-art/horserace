package com.horseracing.service;

import com.horseracing.dto.horse.HorseCreateRequest;
import com.horseracing.dto.horse.HorseResponse;
import com.horseracing.dto.horse.HorseUpdateRequest;
import com.horseracing.entity.Horse;
import com.horseracing.entity.User;
import com.horseracing.enums.HorseStatus;
import com.horseracing.exception.ForbiddenException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.HorseRepository;
import com.horseracing.repository.UserRepository;
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
public class HorseService {

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<HorseResponse> getAllHorses(Pageable pageable) {
        return horseRepository.findAllActive(pageable)
            .map(this::mapToResponse);
    }

    public Page<HorseResponse> searchHorses(String keyword, Pageable pageable) {
        return horseRepository.searchByKeyword(keyword, pageable)
            .map(this::mapToResponse);
    }

    public Page<HorseResponse> getHorsesByStatus(HorseStatus status, Pageable pageable) {
        return horseRepository.findByStatus(status, pageable)
            .map(this::mapToResponse);
    }

    public Page<HorseResponse> getHorsesByOwner(Long ownerId, Pageable pageable) {
        return horseRepository.findByOwner(ownerId, pageable)
            .map(this::mapToResponse);
    }

    public HorseResponse getHorseById(Long id) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));
        return mapToResponse(horse);
    }

    @Transactional
    public HorseResponse createHorse(Long ownerId, HorseCreateRequest request) {
        log.info("Creating new horse for owner: {}", ownerId);

        // Check if code already exists
        if (horseRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Horse code already exists");
        }

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Horse horse = Horse.builder()
            .name(request.getName())
            .code(request.getCode())
            .breed(request.getBreed())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .weight(request.getWeight())
            .description(request.getDescription())
            .status(HorseStatus.PENDING)
            .ownerId(ownerId)
            .build();

        Horse savedHorse = horseRepository.save(horse);
        log.info("Horse created with id: {}", savedHorse.getId());

        return mapToResponse(savedHorse);
    }

    @Transactional
    public HorseResponse updateHorse(Long id, Long ownerId, HorseUpdateRequest request) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        // Check ownership
        if (!horse.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("You don't have permission to update this horse");
        }

        // Check if code is being changed and if it already exists
        if (request.getCode() != null && !request.getCode().equals(horse.getCode())) {
            if (horseRepository.findByCode(request.getCode()).isPresent()) {
                throw new RuntimeException("Horse code already exists");
            }
            horse.setCode(request.getCode());
        }

        if (request.getName() != null) {
            horse.setName(request.getName());
        }
        if (request.getBreed() != null) {
            horse.setBreed(request.getBreed());
        }
        if (request.getDateOfBirth() != null) {
            horse.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            horse.setGender(request.getGender());
        }
        if (request.getWeight() != null) {
            horse.setWeight(request.getWeight());
        }
        if (request.getDescription() != null) {
            horse.setDescription(request.getDescription());
        }

        horse.setUpdatedAt(LocalDateTime.now());
        Horse updatedHorse = horseRepository.save(horse);
        log.info("Horse updated: {}", id);

        return mapToResponse(updatedHorse);
    }

    @Transactional
    public void deleteHorse(Long id, Long ownerId) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        if (!horse.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("You don't have permission to delete this horse");
        }

        horse.setDeleted(1);
        horse.setUpdatedAt(LocalDateTime.now());
        horseRepository.save(horse);
        log.info("Horse deleted: {}", id);
    }

    @Transactional
    public HorseResponse approveHorse(Long id) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        horse.setStatus(HorseStatus.APPROVED);
        horse.setUpdatedAt(LocalDateTime.now());
        Horse updatedHorse = horseRepository.save(horse);
        log.info("Horse approved: {}", id);

        return mapToResponse(updatedHorse);
    }

    @Transactional
    public HorseResponse rejectHorse(Long id, String reason) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        horse.setStatus(HorseStatus.REJECTED);
        horse.setRejectionReason(reason);
        horse.setUpdatedAt(LocalDateTime.now());
        Horse updatedHorse = horseRepository.save(horse);
        log.info("Horse rejected: {}", id);

        return mapToResponse(updatedHorse);
    }

    @Transactional
    public HorseResponse disqualifyHorse(Long id, String reason) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        horse.setStatus(HorseStatus.DISQUALIFIED);
        horse.setDisqualificationReason(reason);
        horse.setUpdatedAt(LocalDateTime.now());
        Horse updatedHorse = horseRepository.save(horse);
        log.info("Horse disqualified: {}", id);

        return mapToResponse(updatedHorse);
    }

    private HorseResponse mapToResponse(Horse horse) {
        HorseResponse response = modelMapper.map(horse, HorseResponse.class);
        if (horse.getOwnerId() != null) {
            userRepository.findById(horse.getOwnerId()).ifPresent(owner ->
                response.setOwnerName(owner.getFullName())
            );
        }
        return response;
    }
}
