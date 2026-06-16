package com.horseracing.service;

import com.horseracing.dto.request.jockey.JockeyCreateRequest;
import com.horseracing.dto.request.jockey.JockeyUpdateRequest;
import com.horseracing.dto.response.jockey.JockeyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JockeyService {
    Page<JockeyResponse> getAllJockeys(Pageable pageable, String keyword, String status, Long ownerId);
    
    JockeyResponse getJockeyById(Long id);
    
    JockeyResponse createJockey(JockeyCreateRequest request, Long ownerId);
    
    JockeyResponse updateJockey(Long id, JockeyUpdateRequest request, Long ownerId);
    
    void deleteJockey(Long id, Long ownerId);
    
    JockeyResponse approveJockey(Long id);
    
    JockeyResponse rejectJockey(Long id, String reason);
}
