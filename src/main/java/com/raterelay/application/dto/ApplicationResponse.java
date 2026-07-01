package com.raterelay.application.dto;

import com.raterelay.application.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {

    private Long id;
    private String name;
    private Long developerId;
    private String apiKey;
    private Integer requestsPerMinute;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
}