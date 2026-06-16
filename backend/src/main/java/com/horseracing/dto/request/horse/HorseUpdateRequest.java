package com.horseracing.dto.request.horse;

import com.horseracing.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorseUpdateRequest {
    @NotBlank(message = "Horse name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Breed is required")
    @Size(max = 100)
    private String breed;

    private String color;

    @DecimalMin("100")
    @DecimalMax("1200")
    private Double weight;
}
