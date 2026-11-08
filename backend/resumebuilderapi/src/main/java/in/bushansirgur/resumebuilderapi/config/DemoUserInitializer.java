package in.bushansirgur.resumebuilderapi.config;

import in.bushansirgur.resumebuilderapi.document.User;
import in.bushansirgur.resumebuilderapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class DemoUserInitializer {

    private static final String DEMO_EMAIL = "demo@example.com";
    private static final String DEMO_PASSWORD = "Demo@12345";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner createDemoUser() {
        return args -> {
            User demoUser = userRepository.findByEmail(DEMO_EMAIL).orElseGet(() -> User.builder()
                    .name("Resume Builder Demo")
                    .email(DEMO_EMAIL)
                    .subscriptionPlan("Basic")
                    .build());

            demoUser.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            demoUser.setEmailVerified(true);
            demoUser.setVerificationToken(null);
            demoUser.setVerificationExpires(null);
            userRepository.save(demoUser);
            log.info("Demo user created.");
        };
    }
}
