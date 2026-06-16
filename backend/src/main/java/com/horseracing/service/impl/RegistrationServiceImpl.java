package com.horseracing.service.impl;

import com.horseracing.dto.request.registration.RegistrationCreateRequest;
import com.horseracing.dto.request.registration.RegistrationRejectRequest;
import com.horseracing.dto.response.registration.RegistrationResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.*;
import com.horseracing.enums.RegistrationStatus;
import com.horseracing.enums.RaceStatus;
import com.horseracing.enums.HorseStatus;
import com.horseracing.enums.JockeyStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ForbiddenException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.*;
import com.horseracing.service.RegistrationService;
import com.horseracing.service.NotificationService;
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
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RaceRegistrationRepository registrationRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private HorseRepository horseRepository;
    @Autowired
    private JockeyRepository jockeyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestHistoryRepository requestHistoryRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    public RegistrationResponse createRegistration(RegistrationCreateRequest request, Long ownerId) {
        // Validate race
        Race race = raceRepository.findById(request.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found"));
        
        if (!race.getStatus().equals(RaceStatus.OPEN)) {
            throw new BadRequestException("Race must be in OPEN status to register");
        }

        // Validate horse
        Horse horse = horseRepository.findById(request.getHorseId())
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));
        
        if (!horse.getStatus().equals(HorseStatus.APPROVED)) {
            throw new BadRequestException("Horse must be APPROVED to register");
        }
        
        if (!horse.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Horse does not belong to you");
        }

        // Validate jockey
        Jockey jockey = jockeyRepository.findById(request.getJockeyId())
            .orElseThrow(() -> new ResourceNotFoundException("Jockey not found"));
        
        if (!jockey.getStatus().equals(JockeyStatus.APPROVED)) {
            throw new BadRequestException("Jockey must be APPROVED to register");
        }
        
        if (!jockey.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Jockey does not belong to you");
        }

        // Check if horse already registered in this race
        if (registrationRepository.findByRaceIdAndHorseId(race.getId(), horse.getId()).isPresent()) {
            throw new BadRequestException("Horse already registered in this race");
        }

        // Check if jockey already registered in this race
        if (registrationRepository.findByRaceIdAndJockeyId(race.getId(), jockey.getId()).isPresent()) {
            throw new BadRequestException("Jockey already registered in this race");
        }

        // Check race capacity
        long approvedCount = registrationRepository.countByRaceAndStatus(race.getId(), RegistrationStatus.APPROVED);
        if (approvedCount >= race.getMaxHorses()) {
            throw new BadRequestException("Race is full");
        }

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        RaceRegistration registration = RaceRegistration.builder()
            .race(race)
            .horse(horse)
            .jockey(jockey)
            .owner(owner)
            .status(RegistrationStatus.PENDING)
            .registeredAt(LocalDateTime.now())
            .build();

        RaceRegistration saved = registrationRepository.save(registration);
        log.info("Registration created: Horse {} + Jockey {} for Race {}", horse.getCode(), jockey.getId(), race.getId());
        
        return mapToRegistrationResponse(saved);
    }

    @Override
    public RegistrationResponse approveRegistration(Long id, Long approvedBy) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (!registration.getStatus().equals(RegistrationStatus.PENDING)) {
            throw new BadRequestException("Registration must be in PENDING status to approve");
        }

        // Final validation before approval
        if (!registration.getHorse().getStatus().equals(HorseStatus.APPROVED)) {
            throw new BadRequestException("Horse is no longer approved");
        }
        if (!registration.getJockey().getStatus().equals(JockeyStatus.APPROVED)) {
            throw new BadRequestException("Jockey is no longer approved");
        }

        RegistrationStatus oldStatus = registration.getStatus();
        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setReviewedAt(LocalDateTime.now());
        User reviewer = userRepository.findById(approvedBy)
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        registration.setReviewedBy(reviewer);

        RaceRegistration updated = registrationRepository.save(registration);
        
        // Record history
        recordHistory("REGISTRATION", registration.getId(), oldStatus.name(), RegistrationStatus.APPROVED.name(), "Approved", approvedBy);
        
        // Send notification to owner
        notificationService.sendApprovalNotification(registration.getOwner().getId(), 
            "Đăng ký được duyệt", 
            "Ngựa " + registration.getHorse().getName() + " đã được duyệt tham gia cuộc đua " + registration.getRace().getName(),
            "REGISTRATION", registration.getId());

        log.info("Registration approved: {}", id);
        return mapToRegistrationResponse(updated);
    }

    @Override
    public RegistrationResponse rejectRegistration(Long id, RegistrationRejectRequest request, Long rejectedBy) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (!registration.getStatus().equals(RegistrationStatus.PENDING)) {
            throw new BadRequestException("Registration must be in PENDING status to reject");
        }

        RegistrationStatus oldStatus = registration.getStatus();
        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectReason(request.getReason());
        registration.setReviewedAt(LocalDateTime.now());
        User reviewer = userRepository.findById(rejectedBy)
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        registration.setReviewedBy(reviewer);

        RaceRegistration updated = registrationRepository.save(registration);
        
        // Record history
        recordHistory("REGISTRATION", registration.getId(), oldStatus.name(), RegistrationStatus.REJECTED.name(), request.getReason(), rejectedBy);
        
        // Send notification to owner
        notificationService.sendRejectionNotification(registration.getOwner().getId(),
            "Đăng ký bị từ chối",
            "Đăng ký ngựa " + registration.getHorse().getName() + " cho cuộc đua " + registration.getRace().getName() + " đã bị từ chối. Lý do: " + request.getReason(),
            "REGISTRATION", registration.getId());

        log.info("Registration rejected: {}", id);
        return mapToRegistrationResponse(updated);
    }

    @Override
    public RegistrationResponse getRegistration(Long id) {
        RaceRegistration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        return mapToRegistrationResponse(registration);
    }

    @Override
    public PageResponse<RegistrationResponse> getAllRegistrations(int page, int size, Long raceId, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("registeredAt").descending());
        Page<RaceRegistration> regPage;

        if (raceId != null && status != null && !status.isEmpty()) {
            regPage = registrationRepository.findByRaceAndStatus(raceId, RegistrationStatus.valueOf(status), pageable);
        } else if (status != null && !status.isEmpty()) {
            regPage = registrationRepository.findByStatus(RegistrationStatus.valueOf(status), pageable);
        } else if (raceId != null) {
            regPage = registrationRepository.findByRaceAndStatus(raceId, null, pageable);
        } else {
            regPage = registrationRepository.findAll(pageable);
        }

        return PageResponse.<RegistrationResponse>builder()
            .content(regPage.getContent().stream().map(this::mapToRegistrationResponse).collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(regPage.getTotalElements())
            .totalPages(regPage.getTotalPages())
            .last(regPage.isLast())
            .build();
    }

    @Override
    public PageResponse<RegistrationResponse> getMyRegistrations(Long ownerId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("registeredAt").descending());
        Page<RaceRegistration> regPage;

        if (status != null && !status.isEmpty()) {
            // Need custom query in repository
            regPage = registrationRepository.findByOwnerAndStatus(ownerId, RegistrationStatus.valueOf(status), pageable);
        } else {
            regPage = registrationRepository.findByOwner(ownerId, pageable);
        }

        return PageResponse.<RegistrationResponse>builder()
            .content(regPage.getContent().stream().map(this::mapToRegistrationResponse).collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(regPage.getTotalElements())
            .totalPages(regPage.getTotalPages())
            .last(regPage.isLast())
            .build();
    }

    private RegistrationResponse mapToRegistrationResponse(RaceRegistration registration) {
        return RegistrationResponse.builder()
            .id(registration.getId())
            .raceId(registration.getRace().getId())
            .raceName(registration.getRace().getName())
            .raceDate(registration.getRace().getRaceDate().toString())
            .horseId(registration.getHorse().getId())
            .horseName(registration.getHorse().getName())
            .horseCode(registration.getHorse().getCode())
            .jockeyId(registration.getJockey().getId())
            .jockeyName(registration.getJockey().getFullName())
            .ownerId(registration.getOwner().getId())
            .ownerName(registration.getOwner().getFullName())
            .status(registration.getStatus().name())
            .rejectReason(registration.getRejectReason())
            .registeredAt(registration.getRegisteredAt().toString())
            .reviewedAt(registration.getReviewedAt() != null ? registration.getReviewedAt().toString() : null)
            .build();
    }

    private void recordHistory(String targetType, Long targetId, String fromStatus, String toStatus, String reason, Long actionBy) {
        RequestHistory history = RequestHistory.builder()
            .targetType(targetType)
            .targetId(targetId)
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .reason(reason)
            .actionBy(userRepository.findById(actionBy).orElse(null))
            .build();
        requestHistoryRepository.save(history);
    }
}
