package in.bushansirgur.resumebuilderapi.service;

import in.bushansirgur.resumebuilderapi.document.User;
import in.bushansirgur.resumebuilderapi.dto.AuthResponse;
import in.bushansirgur.resumebuilderapi.dto.LoginRequest;
import in.bushansirgur.resumebuilderapi.dto.RegisterRequest;
import in.bushansirgur.resumebuilderapi.exception.AuthenticationException;
import in.bushansirgur.resumebuilderapi.exception.ExternalServiceException;
import in.bushansirgur.resumebuilderapi.exception.ResourceExistsException;
import in.bushansirgur.resumebuilderapi.repository.UserRepository;
import in.bushansirgur.resumebuilderapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    @Value("${app.email.verification.enabled:true}")
    private boolean emailVerificationEnabled;

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists with this email");
        }

        User newUser = toDocument(request);

        if (!emailVerificationEnabled) {
            newUser.setEmailVerified(true);
            newUser.setVerificationToken(null);
            newUser.setVerificationExpires(null);
        }

        User savedUser = userRepository.save(newUser);
        if (emailVerificationEnabled) {
            try {
                sendVerificationEmail(savedUser);
            } catch (ExternalServiceException ex) {
                log.warn("Verification email could not be sent for {}. The user account is retained for resend attempts.",
                        savedUser.getEmail(), ex);
            }
        }

        return toResponse(savedUser);
    }

    private void sendVerificationEmail(User newUser) {
        log.info("Sending verification email to: {}", newUser.getEmail());
        try {
            String link = appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>" +
                    "<h2>Verify your email</h2>" +
                    "<p>Hi " + newUser.getName() + ", please confirm your email to activate your account.</p>" +
                    "<p><a href='" + link
                    + "' style='display:inline-block;padding:10px 16px;background:#6366f1;color:#fff;border-radius:6px;text-decoration:none'>Verify Email</a></p>"
                    +
                    "<p>Or copy this link: " + link + "</p>" +
                    "<p>This link expires in 24 hours.</p>" +
                    "</div>";
            emailService.sendHtmlEmail(newUser.getEmail(), "Verify your email", html);
        }catch (Exception e) {
            log.error("Verification email delivery failed for {}", newUser.getEmail(), e);
            throw new ExternalServiceException(
                    "We could not send the verification email. Please try again later.", e);
        }
    }

    private AuthResponse toResponse(User newUser) {
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toDocument(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(!emailVerificationEnabled)
                .verificationToken(emailVerificationEnabled ? UUID.randomUUID().toString() : null)
                .verificationExpires(emailVerificationEnabled ? LocalDateTime.now().plusHours(24) : null)
                .build();
    }

    public void verifyEmail(String token) {
        log.info("Inside AuthService: verifyEmail(): {}", token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token"));

        if (user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired. Please request new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!request.getPassword().equals(existingUser.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        if (emailVerificationEnabled && !existingUser.isEmailVerified()) {
            throw new AuthenticationException("Please verify your email before logging in.");
        }

        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = toResponse(existingUser);
        response.setToken(token);
        return response;

    }

    public void resendVerification(String email) {
        //Step 1: Fetch the user account by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Step 2: Check the email is verified
        if (user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified.");
        }

        //Step 3: Set the new verification token and expires time
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));

        //Step 4: Update the user
        userRepository.save(user);

        //Step 5: Resend the verification email
        sendVerificationEmail(user);

    }

    public AuthResponse getProfile(Object principalObject) {
        User existingUser = (User) principalObject;
        return toResponse(existingUser);
    }
}
