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
public class HorseCreateRequest {
    @NotBlank(message = "Horse name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Horse code is required")
    @Size(min = 2, max = 20)
    private String code;

    @Size(max = 100)
    private String breed;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    private String description;
}
