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
public class JockeyCreateRequest {
    @NotBlank(message = "Jockey name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "License number is required")
    @Size(min = 2, max = 50)
    private String licenseNumber;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @Size(max = 500)
    private String biography;
}
