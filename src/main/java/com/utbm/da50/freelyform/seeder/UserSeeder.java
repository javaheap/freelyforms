package com.utbm.da50.freelyform.seeder;

import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Retry settings
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 1000; // 1 seconds

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
    }

    private void seedAdminUser() {
        int retryCount = 0;
        boolean isConnected = false;

        // Retry logic to wait until MongoDB is ready
        while (retryCount < MAX_RETRIES && !isConnected) {
            try {
                userRepository.count(); // Simple check to see if MongoDB is connected
                isConnected = true; // If this doesn't throw an exception, MongoDB is connected
                System.out.println("MongoDB connection established.");
            } catch (Exception e) {
                retryCount++;
                System.err.println("MongoDB connection failed. Retrying " + retryCount + "/" + MAX_RETRIES + "...");
                if (retryCount >= MAX_RETRIES) {
                    System.err.println("Could not establish MongoDB connection after " + MAX_RETRIES + " attempts.");
                    return; // Exit if connection fails after max retries
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // MongoDB is connected, proceed with seeding the admin user
        String adminEmail = "admin@freelyform.com";

        // Check if an admin user already exists
        boolean adminExists = userRepository.existsByEmail(adminEmail);

        if (!adminExists) {
            // Create the admin user
            User adminUser = new User();
            adminUser.setFirstName("Default");
            adminUser.setLastName("Admin");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(new HashSet<>(Arrays.asList(UserRole.ADMIN, UserRole.USER)));

            userRepository.save(adminUser);
            System.out.println("Admin user created: " + adminEmail);
        } else {
            System.out.println("Admin user already exists: " + adminEmail);
        }
    }
}

