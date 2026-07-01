package com.raterelay.application;

import com.raterelay.application.dto.ApplicationRequest;
import com.raterelay.application.dto.ApplicationResponse;
import com.raterelay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/developer/applications")
@RequiredArgsConstructor
@Tag(name = "Developer Applications", description = "Authenticated developer's own application management")
public class DeveloperApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create a new application", description = "Generates an API key for the new application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> create(@Valid @RequestBody ApplicationRequest request) {
        Long developerId = currentDeveloperId();
        ApplicationResponse created = applicationService.createApplication(developerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @GetMapping
    @Operation(summary = "List my applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> listMine() {
        Long developerId = currentDeveloperId();
        return ResponseEntity.ok(ApiResponse.success(applicationService.listMyApplications(developerId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one of my applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getMine(@PathVariable Long id) {
        Long developerId = currentDeveloperId();
        return ResponseEntity.ok(ApiResponse.success(applicationService.getMyApplication(developerId, id)));
    }

    @PostMapping("/{id}/regenerate-key")
    @Operation(summary = "Regenerate my application's API key")
    public ResponseEntity<ApiResponse<ApplicationResponse>> regenerateKey(@PathVariable Long id) {
        Long developerId = currentDeveloperId();
        return ResponseEntity.ok(ApiResponse.success(applicationService.regenerateMyApiKey(developerId, id)));
    }

    // Reads the developerId placed into the security context by JwtAuthFilter
    private Long currentDeveloperId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}