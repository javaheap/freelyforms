package com.utbm.da50.freelyform.controller;

import com.utbm.da50.freelyform.dto.auth.AuthenticationRequest;
import com.utbm.da50.freelyform.dto.auth.AuthenticationResponse;
import com.utbm.da50.freelyform.dto.user.RegisterUserRequest;
import com.utbm.da50.freelyform.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Registers the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "409", description = "Conflict: email already in use"),
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterUserRequest request) {
        return authenticationService.register(request);
    }

    @Operation(summary = "Logins the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: invalid credentials"),
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return authenticationService.login(request);
    }

}
