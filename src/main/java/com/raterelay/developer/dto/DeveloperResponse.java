package com.raterelay.developer.dto;

import com.raterelay.developer.Developer;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DeveloperResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean emailVerified;
    private LocalDateTime createdAt;

    public static DeveloperResponse from(Developer d) {
        return DeveloperResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .email(d.getEmail())
                .emailVerified(d.getEmailVerified())
                .createdAt(d.getCreatedAt())
                .build();
    }
}