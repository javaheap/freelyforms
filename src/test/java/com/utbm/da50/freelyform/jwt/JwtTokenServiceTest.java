package com.utbm.da50.freelyform.jwt;

import com.utbm.da50.freelyform.configuration.JwtTokenService;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.enums.UserRole;  // Assuming you have an enum UserRole
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Mock
    private User userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set the secret key manually
        jwtTokenService.setKey("e94e2e8bdca8e6a7b99d0a681ce81b8ce9517249304198c85501329842e0796d"); // Set valid base64 key

        // Mock the user details
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getId()).thenReturn("123");
        when(userDetails.getFirstName()).thenReturn("John");
        when(userDetails.getLastName()).thenReturn("Doe");
        when(userDetails.getRole()).thenReturn(new HashSet<>(Collections.singleton(UserRole.USER)));  // Set a valid role
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenService.generateToken(userDetails);
        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void testIsTokenValid() {
        String token = jwtTokenService.generateToken(userDetails);
        boolean isValid = jwtTokenService.isTokenValid(token, userDetails);
        assertTrue(isValid, "Token should be valid for the correct user");
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtTokenService.generateToken(userDetails);
        assertFalse(jwtTokenService.isTokenExpired(token), "Token should not be expired immediately after generation");
    }

    @Test
    void testExtractUsername() {
        String token = jwtTokenService.generateToken(userDetails);
        String username = jwtTokenService.extractUsername(token);
        assertEquals("testUser", username, "Extracted username should match the expected value");
    }
}
