package utils;

import models.AdminUser;
import models.Project;
import org.junit.jupiter.api.Test;
import services.HealthService;
import services.ProjectService;
import services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Integration test: the real menu runs with typed input, and one service is made to fail.
class ConsoleMenuTest {

    @Test
    void unexpectedErrorIsLoggedAndTheMenuKeepsRunning() {
        ProjectService brokenService = new ProjectService() {
            @Override
            public Project[] getProjects() {
                throw new IllegalStateException("Simulated failure");
            }
        };
        UserService userService = new UserService();
        userService.addUser(new AdminUser("Admin", "admin@test.com"));

        List<LogRecord> records = new ArrayList<>();
        Handler collector = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        AppLogger.get().addHandler(collector);

        HealthService health = new HealthService(brokenService, userService);

        // 1 = Manage Projects, 1 = Browse Catalog (fails), then 4 = Exit from the main menu
        Scanner input = new Scanner("1\n1\n4\n");

        try {
            assertDoesNotThrow(
                    () -> new ConsoleMenu(input, brokenService, userService, health).start());
        } finally {
            AppLogger.get().removeHandler(collector);
        }

        assertTrue(records.stream().anyMatch(record -> record.getLevel() == Level.SEVERE));
        assertEquals(1, health.getErrorCount());
        assertEquals("DEGRADED", health.getStatus());
    }
}
