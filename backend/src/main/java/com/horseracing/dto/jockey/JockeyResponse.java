package com.horseracing.dto.jockey;

import com.horseracing.enums.JockeyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JockeyResponse {
    private Long id;
    private String name;
    private String licenseNumber;
    private LocalDate dateOfBirth;
    private Double weight;
    private String biography;
    private JockeyStatus status;
    private Long ownerId;
    private String ownerName;
    @JsonProperty("avatarUrl")
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
