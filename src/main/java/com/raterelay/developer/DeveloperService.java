package com.raterelay.developer;

import com.raterelay.common.exceptions.*;
import com.raterelay.developer.dto.*;
import com.raterelay.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public DeveloperResponse signup(SignupRequest request) {
        if (developerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }

        Developer developer = Developer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Developer saved = developerRepository.save(developer);

        // V1 simplification — verification token returned directly instead of emailed.
        // Swap this for real SMTP dispatch later without touching the rest of the flow.
        return DeveloperResponse.from(saved);
    }

    @Transactional
    public String verifyEmail(String token) {
        Developer developer = developerRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));

        developer.setEmailVerified(true);
        developer.setVerificationToken(null);
        developerRepository.save(developer);

        return "Email verified successfully. You can now log in.";
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Developer developer = developerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), developer.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!developer.getEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in");
        }

        String token = jwtUtil.generateToken(developer.getId(), developer.getEmail());

        return AuthResponse.builder()
                .token(token)
                .developerId(developer.getId())
                .name(developer.getName())
                .email(developer.getEmail())
                .build();
    }

    // Exposes the raw verification token for V1 testing — visible in the signup
    // response so you can verify without setting up real email. Remove or hide
    // this once real SMTP is wired in.
    @Transactional(readOnly = true)
    public String getVerificationTokenForTesting(String email) {
        Developer developer = developerRepository.findByEmail(email)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
        return developer.getVerificationToken();
    }
}