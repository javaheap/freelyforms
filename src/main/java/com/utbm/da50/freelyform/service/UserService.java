package com.utbm.da50.freelyform.service;


import com.utbm.da50.freelyform.dto.user.UserRoleRequest;
import com.utbm.da50.freelyform.dto.user.UserSimpleResponse;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    // Business logic in service
    private final UserRepository userRepository;

    // Dependency injection with auto-wiring
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Returns all the users
    public List<UserSimpleResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(User::toUserSimpleResponse)
                .toList();
    }

    // Get user by id
    public UserSimpleResponse findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return user.get().toUserSimpleResponse();
    }


    // Update the roles of the user
    public void updateRoles(String id, @NonNull UserRoleRequest userRoleRequest) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User userToUpdate = user.get();
        userToUpdate.setRole(userRoleRequest.getRoles());
        userToUpdate.getRole().add(UserRole.USER); // Default role

        userRepository.save(userToUpdate);
    }


    // Delete user by id
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }




}
