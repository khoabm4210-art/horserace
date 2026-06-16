package com.horseracing.dto.request.registration;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCreateRequest {
    @NotNull(message = "Race ID is required")
    private Long raceId;
    
    @NotNull(message = "Horse ID is required")
    private Long horseId;
    
    @NotNull(message = "Jockey ID is required")
    private Long jockeyId;
}
