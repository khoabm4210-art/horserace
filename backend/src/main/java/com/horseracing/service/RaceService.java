package com.horseracing.service;

import com.horseracing.dto.request.race.RaceCreateRequest;
import com.horseracing.dto.request.race.RaceUpdateRequest;
import com.horseracing.dto.response.race.RaceResponse;
import com.horseracing.dto.response.registration.RegistrationResponse;
import com.horseracing.dto.response.lane.LaneResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RaceService {
    Page<RaceResponse> getAllRaces(Pageable pageable, Long seasonId, String status, String keyword);
    
    RaceResponse getRaceById(Long id);
    
    RaceResponse createRace(RaceCreateRequest request, Long createdBy);
    
    RaceResponse updateRace(Long id, RaceUpdateRequest request);
    
    void deleteRace(Long id);
    
    RaceResponse publishRace(Long id);
    
    Page<RegistrationResponse> getRaceRegistrations(Long raceId, Pageable pageable, String status);
    
    List<LaneResponse> getRaceLanes(Long raceId);
}
