package com.utbm.da50.freelyform.service;


import com.utbm.da50.freelyform.dto.user.UpdateUserRequest;
import com.utbm.da50.freelyform.dto.user.UserRoleRequest;
import com.utbm.da50.freelyform.dto.user.UserSimpleResponse;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.repository.AnswerRepository;
import com.utbm.da50.freelyform.repository.PrefabRepository;
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
    private final PrefabRepository prefabRepository;
    private final AnswerRepository answerRepository;

    // Dependency injection with auto-wiring
    @Autowired
    public UserService(UserRepository userRepository, PrefabRepository prefabRepository, AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.prefabRepository = prefabRepository;
        this.answerRepository = answerRepository;
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

    public User getUserById(@NonNull String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID '" + userId + "' doesn't exist."));
    }

    public User updateUser(@NonNull String userId, UpdateUserRequest user) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID '" + userId + "' doesn't exist."));

        // Implement updates here
        // e.g., userToUpdate.setName(user.getName()); // Update properties as necessary

        userRepository.save(userToUpdate);
        return userToUpdate;
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


    // Delete user by id and all related forms and answers
    public void deleteById(String id) {
        // TODO : uncomment after answers are done (delete all answers by user id)
        answerRepository.deleteByUserId(id);
        // Delete related forms
        prefabRepository.deleteByUserId(id);
        // Delete the user
        userRepository.deleteById(id);
    }
}
