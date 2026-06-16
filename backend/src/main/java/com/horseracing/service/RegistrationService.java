package com.horseracing.service;

import com.horseracing.dto.request.registration.RegistrationCreateRequest;
import com.horseracing.dto.request.registration.RegistrationRejectRequest;
import com.horseracing.dto.response.registration.RegistrationResponse;
import com.horseracing.dto.response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationService {
    RegistrationResponse createRegistration(RegistrationCreateRequest request, Long ownerId);
    RegistrationResponse approveRegistration(Long id, Long approvedBy);
    RegistrationResponse rejectRegistration(Long id, RegistrationRejectRequest request, Long rejectedBy);
    RegistrationResponse getRegistration(Long id);
    PageResponse<RegistrationResponse> getAllRegistrations(int page, int size, Long raceId, String status);
    PageResponse<RegistrationResponse> getMyRegistrations(Long ownerId, int page, int size, String status);
}
