package com.devops.project.devops_project;

import org.junit.jupiter.api.Test;

class DevopsProjectApplicationMainTest {

    @Test
    void mainShouldStartApplication() {
        DevopsProjectApplication.main(new String[]{
                "--spring.main.web-application-type=none",
                "--spring.main.banner-mode=off",
                "--spring.main.lazy-initialization=true",
                "--spring.main.register-shutdown-hook=false",
                "--security.jwt.secret=12345678901234567890123456789012",
                "--security.jwt.expiration-ms=3600000",
                "--app.admin.username=admin",
                "--app.admin.email=admin@example.com",
                "--app.admin.password=admin123"
        });
    }
}
