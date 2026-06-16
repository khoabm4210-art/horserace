package com.horseracing.dto.horse;

import com.horseracing.enums.Gender;
import com.horseracing.enums.HorseStatus;
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
public class HorseResponse {
    private Long id;
    private String name;
    private String code;
    private String breed;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Double weight;
    private String description;
    private HorseStatus status;
    private Long ownerId;
    private String ownerName;
    @JsonProperty("avatarUrl")
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
