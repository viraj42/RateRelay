package com.raterelay.developer;

import com.raterelay.common.ApiResponse;
import com.raterelay.developer.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Developer Auth", description = "Signup, email verification, and login for developers")
public class AuthController {

    private final DeveloperService developerService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new developer account")
    public ResponseEntity<ApiResponse<DeveloperResponse>> signup(@Valid @RequestBody SignupRequest request) {
        DeveloperResponse created = developerService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email using the token from signup")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        String result = developerService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = developerService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // V1 testing convenience — see DeveloperService note. Remove once real email is
    // wired in.
    @GetMapping("/dev-verification-token")
    @Operation(summary = "[Testing only] Fetch verification token without email")
    public ResponseEntity<ApiResponse<String>> getVerificationToken(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.success(developerService.getVerificationTokenForTesting(email)));
    }
}