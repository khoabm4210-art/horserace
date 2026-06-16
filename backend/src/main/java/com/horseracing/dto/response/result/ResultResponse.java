package com.horseracing.dto.response.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Long id;
    private Long raceId;
    private String raceName;
    private Boolean isPublished;
    private String publishedAt;
    private List<ResultDetailResponse> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDetailResponse {
        private Integer finishPosition;
        private Long horseId;
        private String horseName;
        private String horseCode;
        private Long jockeyId;
        private String jockeyName;
        private Integer laneNumber;
        private String finishTime;
        private Integer pointsEarned;
        private String notes;
    }
}
