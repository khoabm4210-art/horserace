package com.horseracing.dto.request.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRejectRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
