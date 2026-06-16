package com.horseracing.dto.request.lane;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaneAssignRequest {
    @NotNull(message = "Registration ID is required")
    private Long registrationId;
    
    @NotNull(message = "Lane number is required")
    @Min(1)
    @Max(30)
    private Integer laneNumber;
}
