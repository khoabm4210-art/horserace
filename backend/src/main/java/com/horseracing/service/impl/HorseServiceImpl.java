package com.horseracing.service.impl;

import com.horseracing.dto.request.horse.HorseCreateRequest;
import com.horseracing.dto.request.horse.HorseUpdateRequest;
import com.horseracing.dto.request.horse.HorseRejectRequest;
import com.horseracing.dto.response.horse.HorseResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.Horse;
import com.horseracing.entity.RequestHistory;
import com.horseracing.entity.User;
import com.horseracing.enums.HorseStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.ForbiddenException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.HorseRepository;
import com.horseracing.repository.RequestHistoryRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.HorseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class HorseServiceImpl implements HorseService {
    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Override
    public HorseResponse createHorse(HorseCreateRequest request, Long ownerId) {
        if (horseRepository.findByCode(request.getCode()).isPresent()) {
            throw new BadRequestException("Horse code already exists");
        }

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Horse horse = Horse.builder()
            .code(request.getCode())
            .name(request.getName())
            .breed(request.getBreed())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .color(request.getColor())
            .weight(request.getWeight())
            .owner(owner)
            .status(HorseStatus.PENDING)
            .deleted(0)
            .build();

        Horse savedHorse = horseRepository.save(horse);
        log.info("Horse created: {}", savedHorse.getCode());
        return mapToHorseResponse(savedHorse);
    }

    @Override
    public HorseResponse updateHorse(Long id, HorseUpdateRequest request, Long ownerId) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("You can only update your own horses");
        }

        if (!horse.getStatus().equals(HorseStatus.PENDING)) {
            throw new BadRequestException("Can only update horses in PENDING status");
        }

        horse.setName(request.getName());
        horse.setBreed(request.getBreed());
        horse.setColor(request.getColor());
        horse.setWeight(request.getWeight());

        Horse updatedHorse = horseRepository.save(horse);
        log.info("Horse updated: {}", updatedHorse.getCode());
        return mapToHorseResponse(updatedHorse);
    }

    @Override
    public void deleteHorse(Long id, Long ownerId) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("You can only delete your own horses");
        }

        if (!horse.getStatus().equals(HorseStatus.PENDING)) {
            throw new BadRequestException("Can only delete horses in PENDING status");
        }

        horse.setDeleted(1);
        horseRepository.save(horse);
        log.info("Horse deleted: {}", horse.getCode());
    }

    @Override
    public HorseResponse getHorse(Long id) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));
        return mapToHorseResponse(horse);
    }

    @Override
    public PageResponse<HorseResponse> getAllHorses(int page, int size, String sort, String keyword, 
                                                     String status, Long ownerId) {
        Sort.Order order = Sort.Order.by("createdAt").with(Sort.Direction.DESC);
        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(",");
            Sort.Direction direction = "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
            order = Sort.Order.by(parts[0]).with(direction);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Horse> horsePage;

        if (keyword != null && !keyword.isEmpty()) {
            horsePage = horseRepository.searchActive(keyword, pageable);
        } else if (ownerId != null) {
            horsePage = horseRepository.findByOwnerIdActive(ownerId, pageable);
        } else if (status != null && !status.isEmpty()) {
            horsePage = horseRepository.findByStatusActive(HorseStatus.valueOf(status), pageable);
        } else {
            horsePage = horseRepository.findAllActive(pageable);
        }

        List<HorseResponse> content = horsePage.getContent().stream()
            .map(this::mapToHorseResponse)
            .collect(Collectors.toList());

        return PageResponse.<HorseResponse>builder()
            .content(content)
            .page(page)
            .size(size)
            .totalElements(horsePage.getTotalElements())
            .totalPages(horsePage.getTotalPages())
            .last(horsePage.isLast())
            .build();
    }

    @Override
    public HorseResponse approveHorse(Long id) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getStatus().equals(HorseStatus.PENDING)) {
            throw new BadRequestException("Horse must be in PENDING status to approve");
        }

        HorseStatus oldStatus = horse.getStatus();
        horse.setStatus(HorseStatus.APPROVED);
        Horse updatedHorse = horseRepository.save(horse);

        // Record history
        recordHistory("HORSE", horse.getId(), oldStatus.name(), HorseStatus.APPROVED.name(), "Approved");

        log.info("Horse approved: {}", horse.getCode());
        return mapToHorseResponse(updatedHorse);
    }

    @Override
    public HorseResponse rejectHorse(Long id, HorseRejectRequest request) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getStatus().equals(HorseStatus.PENDING)) {
            throw new BadRequestException("Horse must be in PENDING status to reject");
        }

        HorseStatus oldStatus = horse.getStatus();
        horse.setStatus(HorseStatus.REJECTED);
        Horse updatedHorse = horseRepository.save(horse);

        recordHistory("HORSE", horse.getId(), oldStatus.name(), HorseStatus.REJECTED.name(), request.getReason());

        log.info("Horse rejected: {}", horse.getCode());
        return mapToHorseResponse(updatedHorse);
    }

    @Override
    public HorseResponse disqualifyHorse(Long id, HorseRejectRequest request) {
        Horse horse = horseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Horse not found"));

        if (!horse.getStatus().equals(HorseStatus.APPROVED)) {
            throw new BadRequestException("Horse must be in APPROVED status to disqualify");
        }

        HorseStatus oldStatus = horse.getStatus();
        horse.setStatus(HorseStatus.DISQUALIFIED);
        Horse updatedHorse = horseRepository.save(horse);

        recordHistory("HORSE", horse.getId(), oldStatus.name(), HorseStatus.DISQUALIFIED.name(), request.getReason());

        log.info("Horse disqualified: {}", horse.getCode());
        return mapToHorseResponse(updatedHorse);
    }

    private HorseResponse mapToHorseResponse(Horse horse) {
        return HorseResponse.builder()
            .id(horse.getId())
            .code(horse.getCode())
            .name(horse.getName())
            .breed(horse.getBreed())
            .dateOfBirth(horse.getDateOfBirth().toString())
            .gender(horse.getGender())
            .color(horse.getColor())
            .weight(horse.getWeight())
            .avatarUrl(horse.getAvatarUrl())
            .passportUrl(horse.getPassportUrl())
            .healthCertUrl(horse.getHealthCertUrl())
            .status(horse.getStatus().name())
            .ownerId(horse.getOwner().getId())
            .ownerName(horse.getOwner().getFullName())
            .createdAt(horse.getCreatedAt().toString())
            .build();
    }

    private void recordHistory(String targetType, Long targetId, String fromStatus, String toStatus, String reason) {
        RequestHistory history = RequestHistory.builder()
            .targetType(targetType)
            .targetId(targetId)
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .reason(reason)
            .build();
        requestHistoryRepository.save(history);
    }
}
