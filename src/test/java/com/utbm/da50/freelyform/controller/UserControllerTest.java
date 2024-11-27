package com.utbm.da50.freelyform.controller;

import com.utbm.da50.freelyform.dto.user.UserRoleRequest;
import com.utbm.da50.freelyform.dto.user.UserSimpleResponse;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setRole(new HashSet<>(List.of(UserRole.ADMIN)));


        regularUser = new User();
        regularUser.setId("user123");
        regularUser.setRole(new HashSet<>(List.of(UserRole.USER)));
    }

    @Test
    public void testGetAllUsers_Success() {
        User mockUser = new User();
        mockUser.setId("user12345");
        mockUser.setEmail("test@test.fr");
        mockUser.setRole(new HashSet<>(List.of(UserRole.USER)));

        when(userService.findAll()).thenReturn(List.of(mockUser.toUserSimpleResponse()));

        ResponseEntity<List<UserSimpleResponse>> response = userController.getAllUsers(adminUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody(), hasSize(1));
        verify(userService, times(1)).findAll();
    }

    @Test
    public void testGetAllUsers_Forbidden() {
        ResponseEntity<List<UserSimpleResponse>> response = userController.getAllUsers(regularUser);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(userService, never()).findAll();
    }

    @Test
    public void testUpdateUserRole_Success() {
        UserRoleRequest roleRequest = new UserRoleRequest();
        roleRequest.setRoles(new HashSet<>(List.of(UserRole.USER)));

        ResponseEntity<User> response = userController.updateUserRole(adminUser, "user123", roleRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(userService, times(1)).updateRoles("user123", roleRequest);
    }

    @Test
    public void testUpdateUserRole_Forbidden() {
        UserRoleRequest roleRequest = new UserRoleRequest();
        roleRequest.setRoles(new HashSet<>(List.of(UserRole.USER)));

        ResponseEntity<User> response = userController.updateUserRole(regularUser, "user123", roleRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(userService, never()).updateRoles(any(), any());
    }

    @Test
    public void testDeleteUser_Success() {
        ResponseEntity<User> response = userController.deleteUser(adminUser, "user123");

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
        verify(userService, times(1)).deleteById("user123");
    }

    @Test
    public void testDeleteUser_Forbidden() {
        ResponseEntity<User> response = userController.deleteUser(regularUser, "user123");

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(userService, never()).deleteById(any());
    }
}
