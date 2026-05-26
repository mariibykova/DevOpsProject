package com.devops.project.devops_project.security;

import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameShouldReturnSecurityUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hash");
        user.setEnabled(true);
        user.getRoles().add(Role.ROLE_USER);
        user.getRoles().add(Role.ROLE_ADMIN);

        when(userRepository.findWithRolesByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("user@example.com");

        assertEquals("user@example.com", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertTrue(details.isEnabled());
        assertEquals(2, details.getAuthorities().size());
    }

    @Test
    void loadUserByUsernameShouldSetDisabledFlag() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hash");
        user.setEnabled(false);
        user.getRoles().add(Role.ROLE_USER);

        when(userRepository.findWithRolesByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("user@example.com");

        assertFalse(details.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenMissing() {
        when(userRepository.findWithRolesByEmail("none@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("none@example.com"));

        assertTrue(ex.getMessage().contains("none@example.com"));
    }
}
