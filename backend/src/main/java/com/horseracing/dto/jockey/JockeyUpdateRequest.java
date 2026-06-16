package com.horseracing.dto.jockey;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JockeyUpdateRequest {
    @Size(min = 2, max = 100)
    private String name;

    @Size(min = 2, max = 50)
    private String licenseNumber;

    @PastOrPresent(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Positive(message = "Weight must be positive")
    private Double weight;

    @Size(max = 500)
    private String biography;
}
