package com.horseracing.dto.response.horse;

import com.horseracing.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorseResponse {
    private Long id;
    private String code;
    private String name;
    private String breed;
    private String dateOfBirth;
    private Gender gender;
    private String color;
    private Double weight;
    private String avatarUrl;
    private String passportUrl;
    private String healthCertUrl;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String createdAt;
}
