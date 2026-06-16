package com.horseracing.service;

import com.horseracing.dto.request.horse.HorseCreateRequest;
import com.horseracing.dto.request.horse.HorseUpdateRequest;
import com.horseracing.dto.request.horse.HorseRejectRequest;
import com.horseracing.dto.response.horse.HorseResponse;
import com.horseracing.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface HorseService {
    HorseResponse createHorse(HorseCreateRequest request, Long ownerId);
    HorseResponse updateHorse(Long id, HorseUpdateRequest request, Long ownerId);
    void deleteHorse(Long id, Long ownerId);
    HorseResponse getHorse(Long id);
    PageResponse<HorseResponse> getAllHorses(int page, int size, String sort, String keyword, String status, Long ownerId);
    HorseResponse approveHorse(Long id);
    HorseResponse rejectHorse(Long id, HorseRejectRequest request);
    HorseResponse disqualifyHorse(Long id, HorseRejectRequest request);
}
