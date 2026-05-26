package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.ChangePasswordRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.dto.UserUpdateRequest;
import com.devops.project.devops_project.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllShouldDelegate() {
        when(userService.getAll()).thenReturn(List.of(new UserResponse(1L, "u", "u@example.com")));

        List<UserResponse> result = userController.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getByIdShouldDelegate() {
        when(userService.getById(1L)).thenReturn(new UserResponse(1L, "u", "u@example.com"));

        UserResponse result = userController.getById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    void updateShouldDelegate() {
        UserUpdateRequest request = new UserUpdateRequest("user", "user@example.com");
        when(userService.update(1L, request)).thenReturn(new UserResponse(1L, "user", "user@example.com"));

        UserResponse result = userController.update(1L, request);

        assertEquals("user", result.userName());
    }

    @Test
    void changePasswordShouldDelegate() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldpass12", "newpass12");

        userController.changePassword(1L, request);

        verify(userService).changePassword(1L, request);
    }

    @Test
    void deleteShouldDelegate() {
        userController.delete(1L);

        verify(userService).delete(1L);
    }
}
