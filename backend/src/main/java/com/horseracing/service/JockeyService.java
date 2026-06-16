package com.horseracing.service;

import com.horseracing.dto.jockey.JockeyCreateRequest;
import com.horseracing.dto.jockey.JockeyResponse;
import com.horseracing.dto.jockey.JockeyUpdateRequest;
import com.horseracing.entity.Jockey;
import com.horseracing.entity.User;
import com.horseracing.enums.JockeyStatus;
import com.horseracing.exception.ForbiddenException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.JockeyRepository;
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
public class JockeyService {

    @Autowired
    private JockeyRepository jockeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<JockeyResponse> getAllJockeys(Pageable pageable) {
        return jockeyRepository.findAllActive(pageable)
            .map(this::mapToResponse);
    }

    public Page<JockeyResponse> searchJockeys(String keyword, Pageable pageable) {
        return jockeyRepository.searchByKeyword(keyword, pageable)
            .map(this::mapToResponse);
    }

    public Page<JockeyResponse> getJockeysByStatus(JockeyStatus status, Pageable pageable) {
        return jockeyRepository.findByStatus(status, pageable)
            .map(this::mapToResponse);
    }

    public Page<JockeyResponse> getJockeysByOwner(Long ownerId, Pageable pageable) {
        return jockeyRepository.findByOwner(ownerId, pageable)
            .map(this::mapToResponse);
    }

    public JockeyResponse getJockeyById(Long id) {
        Jockey jockey = jockeyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + id));
        return mapToResponse(jockey);
    }

    @Transactional
    public JockeyResponse createJockey(Long ownerId, JockeyCreateRequest request) {
        log.info("Creating new jockey for owner: {}", ownerId);

        // Check if license number already exists
        if (jockeyRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("License number already exists");
        }

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Jockey jockey = Jockey.builder()
            .name(request.getName())
            .licenseNumber(request.getLicenseNumber())
            .dateOfBirth(request.getDateOfBirth())
            .weight(request.getWeight())
            .biography(request.getBiography())
            .status(JockeyStatus.PENDING)
            .ownerId(ownerId)
            .build();

        Jockey savedJockey = jockeyRepository.save(jockey);
        log.info("Jockey created with id: {}", savedJockey.getId());

        return mapToResponse(savedJockey);
    }

    @Transactional
    public JockeyResponse updateJockey(Long id, Long ownerId, JockeyUpdateRequest request) {
        Jockey jockey = jockeyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + id));

        // Check ownership
        if (!jockey.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("You don't have permission to update this jockey");
        }

        // Check if license number is being changed and if it already exists
        if (request.getLicenseNumber() != null && !request.getLicenseNumber().equals(jockey.getLicenseNumber())) {
            if (jockeyRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
                throw new RuntimeException("License number already exists");
            }
            jockey.setLicenseNumber(request.getLicenseNumber());
        }

        if (request.getName() != null) {
            jockey.setName(request.getName());
        }
        if (request.getDateOfBirth() != null) {
            jockey.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getWeight() != null) {
            jockey.setWeight(request.getWeight());
        }
        if (request.getBiography() != null) {
            jockey.setBiography(request.getBiography());
        }

        jockey.setUpdatedAt(LocalDateTime.now());
        Jockey updatedJockey = jockeyRepository.save(jockey);
        log.info("Jockey updated: {}", id);

        return mapToResponse(updatedJockey);
    }

    @Transactional
    public void deleteJockey(Long id, Long ownerId) {
        Jockey jockey = jockeyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + id));

        if (!jockey.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("You don't have permission to delete this jockey");
        }

        jockey.setDeleted(1);
        jockey.setUpdatedAt(LocalDateTime.now());
        jockeyRepository.save(jockey);
        log.info("Jockey deleted: {}", id);
    }

    @Transactional
    public JockeyResponse approveJockey(Long id) {
        Jockey jockey = jockeyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + id));

        jockey.setStatus(JockeyStatus.APPROVED);
        jockey.setUpdatedAt(LocalDateTime.now());
        Jockey updatedJockey = jockeyRepository.save(jockey);
        log.info("Jockey approved: {}", id);

        return mapToResponse(updatedJockey);
    }

    @Transactional
    public JockeyResponse rejectJockey(Long id, String reason) {
        Jockey jockey = jockeyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + id));

        jockey.setStatus(JockeyStatus.REJECTED);
        jockey.setRejectionReason(reason);
        jockey.setUpdatedAt(LocalDateTime.now());
        Jockey updatedJockey = jockeyRepository.save(jockey);
        log.info("Jockey rejected: {}", id);

        return mapToResponse(updatedJockey);
    }

    private JockeyResponse mapToResponse(Jockey jockey) {
        JockeyResponse response = modelMapper.map(jockey, JockeyResponse.class);
        if (jockey.getOwnerId() != null) {
            userRepository.findById(jockey.getOwnerId()).ifPresent(owner ->
                response.setOwnerName(owner.getFullName())
            );
        }
        return response;
    }
}
