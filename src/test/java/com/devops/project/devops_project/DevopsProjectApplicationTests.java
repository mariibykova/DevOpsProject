package com.devops.project.devops_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"security.jwt.secret=test-secret-key-that-is-long-enough",
		"security.jwt.expiration-ms=3600000",
		"app.admin.username=admin",
		"app.admin.email=admin@example.com",
		"app.admin.password=admin123"
})
class DevopsProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}
