package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.ChangePasswordRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.dto.UserUpdateRequest;
import com.devops.project.devops_project.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationService.isUserOwnerOrAdmin(#id, authentication)")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationService.isUserOwnerOrAdmin(#id, authentication)")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/password")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.isUserOwner(#id, authentication)")
    public void changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.isUserOwnerOrAdmin(#id, authentication)")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
