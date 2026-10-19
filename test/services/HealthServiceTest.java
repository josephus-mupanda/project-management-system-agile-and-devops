package services;

import models.AdminUser;
import models.SoftwareProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HealthServiceTest {

    private HealthService health;

    @BeforeEach
    void setUp() {
        ProjectService projectService = new ProjectService();
        projectService.addProject(new SoftwareProject("App", "d", 100, 1, 1, "Java"));
        UserService userService = new UserService();
        userService.addUser(new AdminUser("Admin", "admin@test.com"));

        health = new HealthService(projectService, userService);
    }

    @Test
    void statusIsUpWhenNoErrorHappened() {
        assertEquals("UP", health.getStatus());
        assertEquals(0, health.getErrorCount());
    }

    @Test
    void statusIsDegradedAfterAnError() {
        health.recordError();
        health.recordError();

        assertEquals("DEGRADED", health.getStatus());
        assertEquals(2, health.getErrorCount());
    }

    @Test
    void uptimeIsNeverNegative() {
        assertFalse(health.getUptime().isNegative());
    }

    @Test
    void uptimeIsFormattedInHoursMinutesSeconds() {
        assertEquals("0h 00m 05s", HealthService.formatUptime(Duration.ofSeconds(5)));
        assertEquals("1h 02m 03s", HealthService.formatUptime(Duration.ofSeconds(3723)));
    }
}
