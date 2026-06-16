package com.horseracing.dto.request.horse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorseRejectRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
