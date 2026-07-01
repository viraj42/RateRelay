package com.raterelay.application;

import com.raterelay.application.dto.ApplicationRequest;
import com.raterelay.application.dto.ApplicationResponse;
import com.raterelay.common.exceptions.ApplicationNotFoundException;
import com.raterelay.developer.Developer;
import com.raterelay.developer.DeveloperRepository;
import com.raterelay.common.exceptions.DeveloperNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DeveloperRepository developerRepository;

    // ── Developer-facing — create an application under their own account ──
    @Transactional
    public ApplicationResponse createApplication(Long developerId, ApplicationRequest request) {
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found: " + developerId));

        Application application = Application.builder()
                .name(request.getName())
                .developer(developer)
                .requestsPerMinute(request.getRequestsPerMinute())
                .apiKey(generateApiKey())
                .build();

        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    // ── Developer-facing — list only their own applications ────────────
    @Transactional(readOnly = true)
    public List<ApplicationResponse> listMyApplications(Long developerId) {
        return applicationRepository.findByDeveloperId(developerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Developer-facing — get one of their own applications ───────────
    @Transactional(readOnly = true)
    public ApplicationResponse getMyApplication(Long developerId, Long applicationId) {
        Application app = applicationRepository.findByIdAndDeveloperId(applicationId, developerId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found or not owned by this developer: " + applicationId));
        return toResponse(app);
    }

    // ── Developer-facing — regenerate their own application's key ──────
    @Transactional
    public ApplicationResponse regenerateMyApiKey(Long developerId, Long applicationId) {
        Application app = applicationRepository.findByIdAndDeveloperId(applicationId, developerId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found or not owned by this developer: " + applicationId));
        app.setApiKey(generateApiKey());
        return toResponse(applicationRepository.save(app));
    }

    // ── Admin-facing — view/manage any application ──────────────────────
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> listAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public void deactivateApplication(Long id) {
        Application app = findById(id);
        app.setStatus(ApplicationStatus.SUSPENDED);
        applicationRepository.save(app);
    }

    @Transactional
    public void reactivateApplication(Long id) {
        Application app = findById(id);
        app.setStatus(ApplicationStatus.ACTIVE);
        applicationRepository.save(app);
    }

    // ── Called by GatewayFilter pipeline — Stage 2 lookup ───────────────
    @Transactional(readOnly = true)
    public Application findByApiKey(String apiKey) {
        return applicationRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ApplicationNotFoundException("Invalid API Key"));
    }

    // ── Private helpers ──────────────────────────────────────────────

    private Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found: " + id));
    }

    private String generateApiKey() {
        return "rl_" + UUID.randomUUID().toString().replace("-", "");
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .developerId(app.getDeveloper().getId())
                .apiKey(app.getApiKey())
                .requestsPerMinute(app.getRequestsPerMinute())
                .status(app.getStatus())
                .createdAt(app.getCreatedAt())
                .build();
    }
}