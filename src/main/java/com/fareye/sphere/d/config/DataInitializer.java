package com.fareye.sphere.d.config;

import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.system.email}")
    private String systemEmail;

    @Value("${app.system.password}")
    private String systemPassword;

    @Override
    public void run(String... args) throws Exception {
        List<User> systemUsers = userRepository.findByRole(Role.SYSTEM);
        
        if (systemUsers.isEmpty()) {
            User systemUser = new User();
            systemUser.setEmail(systemEmail);
            systemUser.setFullName("System Admin");
            systemUser.setPassword(passwordEncoder.encode(systemPassword));
            systemUser.setRole(Role.SYSTEM);
            
            userRepository.save(systemUser);
            System.out.println("Default SYSTEM user successfully created in the database.");
        }
    }
}