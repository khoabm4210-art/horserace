package com.horseracing.dto.response.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;
    private Long raceId;
    private String raceName;
    private String raceDate;
    private Long horseId;
    private String horseName;
    private String horseCode;
    private Long jockeyId;
    private String jockeyName;
    private Long ownerId;
    private String ownerName;
    private String status;
    private String rejectReason;
    private String registeredAt;
    private String reviewedAt;
}
