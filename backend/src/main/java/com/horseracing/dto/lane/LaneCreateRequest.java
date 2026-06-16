package com.horseracing.dto.lane;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaneCreateRequest {
    @NotNull(message = "Registration ID is required")
    private Long registrationId;

    @NotNull(message = "Lane number is required")
    @Positive(message = "Lane number must be positive")
    @Max(value = 30, message = "Lane number cannot exceed 30")
    private Integer laneNumber;
}
