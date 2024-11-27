package com.utbm.da50.freelyform.jwt;

import com.utbm.da50.freelyform.configuration.JwtAuthenticationFilter;
import com.utbm.da50.freelyform.configuration.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {



    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtTokenService jwtTokenService;
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        // Clear SecurityContext before each test
        SecurityContextHolder.clearContext();
        jwtTokenService = mock(JwtTokenService.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenService, userDetailsService);
    }


    @Test
    public void testDoFilterWithValidToken() throws Exception {
        // Arrange: Set up the request with a valid Authorization header
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Set the Authorization header with a valid token
        request.addHeader("Authorization", "Bearer valid_token");

        // Mock the behavior of jwtTokenService
        when(jwtTokenService.extractUsername("valid_token")).thenReturn("user@example.com");
        when(jwtTokenService.isTokenValid(eq("valid_token"), any(UserDetails.class))).thenReturn(true);

        // Mock the user details service
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        // Act: Perform the filter action
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Assert: Ensure that the SecurityContext now holds an authentication object
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be null for valid token");
    }


    @Test
    public void testDoFilterWithInvalidToken() throws Exception {
        // Arrange: Set up the request with an invalid Authorization header
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Set the Authorization header with an invalid token
        request.addHeader("Authorization", "Bearer invalid_token");

        // Mock the behavior of jwtTokenService
        when(jwtTokenService.extractUsername("invalid_token")).thenReturn("user@example.com");
        when(jwtTokenService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(false);

        // Mock the user details service
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        // Act: Perform the filter action
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Assert: Ensure that the SecurityContext is still empty (token is invalid)
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be null for invalid token");
    }

    @Test
    public void testDoFilterWithNoAuthorizationHeader() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Assert: Authentication should be null when no Authorization header is present
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null when no Authorization header is provided");
    }


    @Test
    public void testDoFilterWithMalformedAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Set a malformed Authorization header
        request.addHeader("Authorization", "Bearer malformed_token");

        // Act: Perform the filter action
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Assert: Ensure that the SecurityContext is still empty
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be null for malformed token");
    }

}
