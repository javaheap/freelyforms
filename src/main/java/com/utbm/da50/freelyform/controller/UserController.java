package com.utbm.da50.freelyform.controller;


import com.utbm.da50.freelyform.dto.user.UserRoleRequest;
import com.utbm.da50.freelyform.dto.user.UserSimpleResponse;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "User API", description = "Endpoints for managing users")
@RequestMapping("/v1/users")
@RestController
public class UserController {


    @Autowired
    UserService userService;

    // Get all users when user is admin
    @GetMapping("")
    @Operation(summary = "Get all users, only if the authenticated user is an admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserSimpleResponse>> getAllUsers(@AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        try {
            // Check if user is admin
            if(!user.getRole().contains(UserRole.ADMIN)){
                return ResponseEntity.status(403).build();
            }
            List<UserSimpleResponse> users = userService.findAll();
            // Remove auth user from list :
            users = users.stream().filter(u -> !u.getId().equals(user.getId())).toList();
            return ResponseEntity.ok(users);
        }
        catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }


    // Update user role
    @PatchMapping("/{id}")
    @Operation(summary = "Updates the roles of an user, only if the authenticated user is an admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully modified roles"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUserRole(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody UserRoleRequest request) {

        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        try {
            // Check if user is admin
            if(!user.getRole().contains(UserRole.ADMIN)){
                return ResponseEntity.status(403).build();
            }
            userService.updateRoles(id, request);

            return ResponseEntity.status(200).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes an user, only if the authenticated user is an admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> deleteUser(@AuthenticationPrincipal User user, @PathVariable String id) {

        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        try {
            // Check if user is admin
            if(!user.getRole().contains(UserRole.ADMIN)){
                return ResponseEntity.status(403).build();
            }
            userService.deleteById(id);

            return ResponseEntity.status(204).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }

}
