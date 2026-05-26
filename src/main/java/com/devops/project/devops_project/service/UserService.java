package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.ChangePasswordRequest;
import com.devops.project.devops_project.dto.UserCreateRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.dto.UserUpdateRequest;
import com.devops.project.devops_project.exception.ResourceNotFoundException;
import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = findUserById(id);
        return toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getRoles().add(Role.ROLE_USER);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findUserById(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        applyUpdateRequest(user, request.userName(), request.email());
        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findUserById(id);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + id));
    }

    public User findByEmail(String email) {
        return userRepository.findWithRolesByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email=" + email));
    }

    private void applyUpdateRequest(User user, String userName, String email) {
        user.setUserName(userName);
        user.setEmail(email);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail()
        );
    }
}
