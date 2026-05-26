package com.devops.project.devops_project.config;

import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminUserInitializer initializer;

    @Test
    void runShouldCreateAdminWhenUserNotExists() throws Exception {
        AppAdminProperties props = props();
        initializer = new AdminUserInitializer(userRepository, passwordEncoder, props);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");

        initializer.run(new DefaultApplicationArguments(new String[]{}));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("admin", saved.getUserName());
        assertEquals("admin@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
        assertTrue(saved.isEnabled());
        assertTrue(saved.getRoles().contains(Role.ROLE_USER));
        assertTrue(saved.getRoles().contains(Role.ROLE_ADMIN));
    }

    @Test
    void runShouldEnsureAdminRoleAndEnabledForExistingUser() throws Exception {
        AppAdminProperties props = props();
        initializer = new AdminUserInitializer(userRepository, passwordEncoder, props);

        User existing = new User();
        existing.setEmail("admin@example.com");
        existing.setEnabled(false);
        existing.getRoles().add(Role.ROLE_USER);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(existing));

        initializer.run(new DefaultApplicationArguments(new String[]{}));

        assertTrue(existing.isEnabled());
        assertTrue(existing.getRoles().contains(Role.ROLE_ADMIN));
        verify(userRepository).save(existing);
    }

    @Test
    void runShouldNotSaveWhenExistingAlreadyAdminAndEnabled() throws Exception {
        AppAdminProperties props = props();
        initializer = new AdminUserInitializer(userRepository, passwordEncoder, props);

        User existing = new User();
        existing.setEmail("admin@example.com");
        existing.setEnabled(true);
        existing.getRoles().add(Role.ROLE_ADMIN);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(existing));

        initializer.run(new DefaultApplicationArguments(new String[]{}));

        verify(userRepository, never()).save(any(User.class));
    }

    private AppAdminProperties props() {
        AppAdminProperties props = new AppAdminProperties();
        props.setUsername("admin");
        props.setEmail("admin@example.com");
        props.setPassword("secret123");
        return props;
    }
}
