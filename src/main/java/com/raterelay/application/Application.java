package com.raterelay.application;

import com.raterelay.developer.Developer;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // NEW — every application belongs to exactly one developer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false)
    private Developer developer;

    @Column(unique = true, nullable = false)
    private String apiKey;

    @Column(nullable = false)
    private Integer requestsPerMinute;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Double tokens;

    private LocalDateTime lastRefillTimestamp;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = ApplicationStatus.ACTIVE;
        this.tokens = (double) this.requestsPerMinute;
        this.lastRefillTimestamp = LocalDateTime.now();
    }
}