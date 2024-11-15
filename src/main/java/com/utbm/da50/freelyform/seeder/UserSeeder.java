package com.utbm.da50.freelyform.seeder;

import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserSeeder.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Retry settings
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 1000; // 1 seconds
    private static final String ADMIN_EMAIL = "admin@freelyform.com";
    private static final int PASSWORD_LENGTH = 12;

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
                logger.info("MongoDB connection established.");
            } catch (Exception e) {
                retryCount++;
                logger.error("MongoDB connection failed. Retrying {}/{}", retryCount, MAX_RETRIES);
                if (retryCount >= MAX_RETRIES) {
                    logger.error("Could not establish MongoDB connection after {} attempts.", MAX_RETRIES);
                    return; // Exit if connection fails after max retries
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Check if an admin user already exists
        boolean adminExists = userRepository.existsByEmail(ADMIN_EMAIL);
        String password = generateRandomPassword();

        if (!adminExists) {
            // Create the admin user
            User adminUser = new User();
            adminUser.setFirstName("Default");
            adminUser.setLastName("Admin");
            adminUser.setEmail(ADMIN_EMAIL);
            adminUser.setPassword(passwordEncoder.encode(password));
            adminUser.setRole(new HashSet<>(Arrays.asList(UserRole.ADMIN, UserRole.USER)));

            userRepository.save(adminUser);
            logger.error("\u001B[31m!!!!!!! ADMIN USER CREATED , EMAIL: {} , PASSWORD: {} !!!!!!\u001B[0m", ADMIN_EMAIL, password);
        } else {
            // Update existing admin user with new password
            User adminUser = userRepository.findByEmail(ADMIN_EMAIL).get();
            adminUser.setPassword(passwordEncoder.encode(password));
            userRepository.save(adminUser);
            logger.error("\u001B[31m!!!!!!! ADMIN USER UPDATED , EMAIL: {} , NEW PASSWORD: {} !!!!!!\u001B[0m", ADMIN_EMAIL, password);
        }
    }

    // Generate random password of PASSWORD_LENGTH characters
    private String generateRandomPassword(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}