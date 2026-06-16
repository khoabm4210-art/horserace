package com.horseracing.dto.request.result;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultEntryRequest {
    @NotNull(message = "Race ID is required")
    private Long raceId;
    
    @NotEmpty(message = "Result details cannot be empty")
    private List<ResultDetailItem> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDetailItem {
        @NotNull(message = "Registration ID is required")
        private Long registrationId;
        
        @NotNull(message = "Finish position is required")
        @Min(1)
        private Integer finishPosition;
        
        private String finishTime;
        private String notes;
    }
}
