package com.raterelay.application;

import com.raterelay.application.dto.ApplicationResponse;
import com.raterelay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
@Tag(name = "Admin — Application Oversight", description = "Admin view and moderation of all developer applications")
public class AdminApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "List all applications across all developers")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> list(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.listAllApplications(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get any application by ID")
    public ResponseEntity<ApiResponse<ApplicationResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplication(id)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Suspend any application")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        applicationService.deactivateApplication(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate any application")
    public ResponseEntity<ApiResponse<Void>> reactivate(@PathVariable Long id) {
        applicationService.reactivateApplication(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}