package com.devops.project.devops_project.config;

import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppAdminProperties appAdminProperties;

    public AdminUserInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AppAdminProperties appAdminProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appAdminProperties = appAdminProperties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        userRepository.findByEmail(appAdminProperties.getEmail())
                .ifPresentOrElse(this::ensureAdminRole, this::createAdminUser);
    }

    private void ensureAdminRole(User existingUser) {
        boolean changed = false;

        if (!existingUser.getRoles().contains(Role.ROLE_ADMIN)) {
            existingUser.getRoles().add(Role.ROLE_ADMIN);
            changed = true;
        }

        if (!existingUser.isEnabled()) {
            existingUser.setEnabled(true);
            changed = true;
        }

        if (changed) {
            userRepository.save(existingUser);
        }
    }

    private void createAdminUser() {
        User user = new User();
        user.setUserName(appAdminProperties.getUsername());
        user.setEmail(appAdminProperties.getEmail());
        user.setPassword(passwordEncoder.encode(appAdminProperties.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        user.getRoles().add(Role.ROLE_ADMIN);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
