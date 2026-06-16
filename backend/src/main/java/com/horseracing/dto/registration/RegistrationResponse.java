package com.horseracing.dto.registration;

import com.horseracing.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;
    private Long raceId;
    private String raceName;
    private Long horseId;
    private String horseName;
    private Long jockeyId;
    private String jockeyName;
    private Long ownerId;
    private RegistrationStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
