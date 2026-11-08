package in.bushansirgur.resumebuilderapi.controller;

import in.bushansirgur.resumebuilderapi.dto.AuthResponse;
import in.bushansirgur.resumebuilderapi.dto.LoginRequest;
import in.bushansirgur.resumebuilderapi.dto.RegisterRequest;
import in.bushansirgur.resumebuilderapi.service.AuthService;
import in.bushansirgur.resumebuilderapi.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static in.bushansirgur.resumebuilderapi.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Inside AuthController - register(): {}", request);
        AuthResponse response = authService.register(request);
        log.info("Response from service: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(VERIFY_EMAIL)
    public RedirectView verifyEmail(@RequestParam String token) {
        log.info("Inside AuthController - verifyEmail(): {}", token);
        try {
            authService.verifyEmail(token);
            return new RedirectView(frontendUrl + "/?emailVerified=success");
        } catch (Exception ex) {
            log.error("Email verification failed", ex);
            String reason = URLEncoder.encode(
                    ex.getMessage() != null ? ex.getMessage() : "Verification failed",
                    StandardCharsets.UTF_8);
            return new RedirectView(frontendUrl + "/?emailVerified=failed&reason=" + reason);
        }
    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        log.info("Inside AuthController - uploadImage()");
        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        //Step 1: Get the email from request
        String email = body.get("email");

        //Step 2: Add the validations
        if (Objects.isNull(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        //Step 3: Call the service method to resend verification link
        authService.resendVerification(email);

        //Step 4: Return response
        return ResponseEntity.ok(Map.of("success", true, "message", "Verification email sent"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication) {
        //Step 1: Get the principal object
        Object principalObject = authentication.getPrincipal();

        //Step 2: Call the service method
        AuthResponse currentProfile = authService.getProfile(principalObject);

        //Step 3: return the response
        return ResponseEntity.ok(currentProfile);
    }

}
