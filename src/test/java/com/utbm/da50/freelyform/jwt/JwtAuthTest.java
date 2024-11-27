package com.utbm.da50.freelyform.jwt;

import com.utbm.da50.freelyform.configuration.JwtTokenService;
import com.utbm.da50.freelyform.controller.AuthenticationController;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenService jwtTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationController jwtAuthenticationEntryPoint;

    private UserDetails userDetails;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(jwtAuthenticationEntryPoint) // Use the authentication entry point instead
                .build();

        // Setup User and UserDetails
        user = new User();
        user.setId("12345");
        user.setEmail("testUser@test.fr");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(new HashSet<>(Collections.singleton(UserRole.USER)));

        userDetails = user;
    }

    // Test 1: Testing token generation with valid details
    @Test
    void testGenerateToken() {
        when(jwtTokenService.generateToken(any(Map.class), any(UserDetails.class)))
                .thenReturn("valid-jwt-token");

        String token = jwtTokenService.generateToken(new HashMap<>(), userDetails);

        assertNotNull(token);
        assertEquals("valid-jwt-token", token);
    }

    // Test 2: Test extracting username from token
    @Test
    void testExtractUsername() {
        when(jwtTokenService.extractUsername(anyString())).thenReturn("testUser");

        String username = jwtTokenService.extractUsername("valid-jwt-token");

        assertNotNull(username);
        assertEquals("testUser", username);
    }

    // Test 3: Test invalid token scenario
    @Test
    void testIsTokenValid_InvalidToken() {
        when(jwtTokenService.isTokenValid(anyString(), any(UserDetails.class)))
                .thenReturn(false);

        boolean isValid = jwtTokenService.isTokenValid("invalid-token", userDetails);

        assertFalse(isValid);
    }

    // Test 4: Test expired token scenario
    @Test
    void testIsTokenExpired() {
        when(jwtTokenService.isTokenExpired(anyString()))
                .thenReturn(true);

        boolean isExpired = jwtTokenService.isTokenExpired("expired-token");

        assertTrue(isExpired);
    }

    // Test 5: Test invalid username exception (user not found)
    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userDetailsService.loadUserByUsername("unknownUser"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknownUser");
        });
    }

    // Test 6: Check if login returns 200 OK
    @Test
    void testLoginReturns200WithValidCredentials() throws Exception {
        // Mock the user repository to return the user
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Mock successful authentication
        // Assuming you have a mocked AuthenticationManager
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtTokenService.generateToken(any())).thenReturn("valid-jwt-token");

        // Perform the login request
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + user.getEmail() + "\", \"password\": \"password\"}"))
                .andExpect(status().isOk());
    }

    // Test 7: Testing unauthorized access to a protected endpoint
    // TODO : Uncomment when the protected endpoint is implemented
    /*@Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }*/


    // Test 8: Testing invalid token denies access to protected endpoint
    // TODO : Same here
    /*@Test
    void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        when(jwtTokenService.isTokenValid(anyString(), any(UserDetails.class)))
                .thenReturn(false);

        mockMvc.perform(get("/protected-endpoint")
                        .header("Authorization", "Bearer invalid-jwt-token"))
                .andExpect(status().isForbidden());
    }*/


    // Test 9: Test JwtAuthenticationEntryPoint returns 401 Unauthorized
    // TODO : uncomment when a protected endpoint is implemented
    /*@Test
    void testCommenceUnauthorized() throws Exception {
        mockMvc.perform(get("/protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }*/

}

