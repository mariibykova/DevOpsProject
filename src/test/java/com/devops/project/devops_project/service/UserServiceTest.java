package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.ChangePasswordRequest;
import com.devops.project.devops_project.dto.UserCreateRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.dto.UserUpdateRequest;
import com.devops.project.devops_project.exception.ResourceNotFoundException;
import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllShouldMapUsers() {
        User first = user(1L, "alice", "alice@example.com", "x", true, Role.ROLE_USER);
        User second = user(2L, "bob", "bob@example.com", "y", true, Role.ROLE_ADMIN);
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        List<UserResponse> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).userName());
        assertEquals("bob@example.com", result.get(1).email());
    }

    @Test
    void getByIdShouldReturnUser() {
        User user = user(10L, "test", "test@example.com", "p", true, Role.ROLE_USER);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getById(10L);

        assertEquals(10L, result.id());
        assertEquals("test", result.userName());
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.getById(9L));

        assertThat(ex.getMessage()).contains("User not found with id=9");
    }

    @Test
    void createShouldSaveEncodedPasswordAndDefaultRole() {
        UserCreateRequest request = new UserCreateRequest("john", "john@example.com", "secret123");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(101L);
            return u;
        });

        UserResponse result = userService.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("john", saved.getUserName());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
        assertTrue(saved.isEnabled());
        assertTrue(saved.getRoles().contains(Role.ROLE_USER));
        assertEquals(101L, result.id());
    }

    @Test
    void createShouldThrowWhenEmailExists() {
        UserCreateRequest request = new UserCreateRequest("john", "john@example.com", "secret123");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.create(request));
        String reason = ex.getReason();

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertNotNull(reason);
        assertThat(reason).contains("Email already exists");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateShouldSaveWhenEmailIsUnique() {
        User existing = user(7L, "old", "old@example.com", "p", true, Role.ROLE_USER);
        UserUpdateRequest request = new UserUpdateRequest("new", "new@example.com");
        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 7L)).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);

        UserResponse result = userService.update(7L, request);

        assertEquals("new", existing.getUserName());
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("new", result.userName());
    }

    @Test
    void updateShouldThrowWhenDuplicateEmail() {
        User existing = user(7L, "old", "old@example.com", "p", true, Role.ROLE_USER);
        UserUpdateRequest request = new UserUpdateRequest("new", "new@example.com");
        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 7L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.update(7L, request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userRepository, never()).save(existing);
    }

    @Test
    void changePasswordShouldSaveWhenOldPasswordMatches() {
        User existing = user(3L, "u", "u@example.com", "old-hash", true, Role.ROLE_USER);
        when(userRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("oldpass", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("newpass123")).thenReturn("new-hash");

        userService.changePassword(3L, new ChangePasswordRequest("oldpass", "newpass123"));

        assertEquals("new-hash", existing.getPassword());
        verify(userRepository).save(existing);
    }

    @Test
    void changePasswordShouldThrowWhenOldPasswordInvalid() {
        User existing = user(3L, "u", "u@example.com", "old-hash", true, Role.ROLE_USER);
        when(userRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword(3L, new ChangePasswordRequest("wrong", "newpass123")));
        String reason = ex.getReason();

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertNotNull(reason);
        assertThat(reason).contains("Old password is incorrect");
        verify(userRepository, never()).save(existing);
    }

    @Test
    void deleteShouldRemoveExistingUser() {
        User existing = user(5L, "u", "u@example.com", "p", true, Role.ROLE_USER);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        userService.delete(5L);

        verify(userRepository).delete(existing);
    }

    @Test
    void findByEmailShouldReturnUser() {
        User existing = user(9L, "u", "u@example.com", "p", false, Role.ROLE_USER);
        when(userRepository.findWithRolesByEmail("u@example.com")).thenReturn(Optional.of(existing));

        User result = userService.findByEmail("u@example.com");

        assertEquals(9L, result.getId());
        assertFalse(result.isEnabled());
    }

    @Test
    void findByEmailShouldThrowWhenMissing() {
        when(userRepository.findWithRolesByEmail("none@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.findByEmail("none@example.com"));

        assertThat(ex.getMessage()).contains("User not found with email=none@example.com");
    }

    private User user(Long id, String name, String email, String password, boolean enabled, Role role) {
        User user = new User();
        user.setId(id);
        user.setUserName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.getRoles().add(role);
        return user;
    }
}
