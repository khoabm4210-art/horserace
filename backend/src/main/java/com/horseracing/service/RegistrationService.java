package com.horseracing.service;

import com.horseracing.dto.request.registration.RegistrationCreateRequest;
import com.horseracing.dto.response.registration.RegistrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegistrationService {
    Page<RegistrationResponse> getAllRegistrations(Pageable pageable, Long raceId, String status);
    
    Page<RegistrationResponse> getMyRegistrations(Long ownerId, Pageable pageable, String status);
    
    RegistrationResponse getRegistrationById(Long id);
    
    RegistrationResponse createRegistration(RegistrationCreateRequest request, Long ownerId);
    
    RegistrationResponse approveRegistration(Long id);
    
    RegistrationResponse rejectRegistration(Long id, String reason);
}
