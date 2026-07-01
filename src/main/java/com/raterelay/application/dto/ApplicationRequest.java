package com.raterelay.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Application name is required")
    private String name;

    @NotNull(message = "requestsPerMinute is required")
    @Min(value = 1, message = "Minimum 1 request per minute")
    @Max(value = 10000, message = "Maximum 10000 requests per minute")
    private Integer requestsPerMinute;
}