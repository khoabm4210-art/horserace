package com.horseracing.dto.horse;

import com.horseracing.enums.Gender;
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
public class HorseUpdateRequest {
    @Size(min = 2, max = 100)
    private String name;

    @Size(min = 2, max = 20)
    private String code;

    @Size(max = 100)
    private String breed;

    @PastOrPresent(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Positive(message = "Weight must be positive")
    private Double weight;

    private String description;
}
