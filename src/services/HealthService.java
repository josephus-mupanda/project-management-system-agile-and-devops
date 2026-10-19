package services;

import utils.AppLogger;
import utils.ConsoleMenu;

import java.time.Duration;
import java.time.Instant;

// The console version of a health endpoint: is the app running fine, and since when?
public class HealthService {

    private final ProjectService projectService;
    private final UserService userService;
    private final Instant startTime;
    private int errorCount;

    public HealthService(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
        this.startTime = Instant.now();
    }

    public void recordError() {
        errorCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    // UP while no unexpected error happened, DEGRADED after the first one.
    public String getStatus() {
        return errorCount == 0 ? "UP" : "DEGRADED";
    }

    public Duration getUptime() {
        return Duration.between(startTime, Instant.now());
    }

    public static String formatUptime(Duration uptime) {
        return String.format("%dh %02dm %02ds",
                uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart());
    }

    public void displayHealth() {
        ConsoleMenu.printHeader("SYSTEM HEALTH");

        System.out.println("Status:             " + getStatus());
        System.out.println("Uptime:             " + formatUptime(getUptime()));
        System.out.println("Projects:           " + projectService.getProjects().length);
        System.out.println("Users:              " + userService.getUsers().length);
        System.out.println("Errors since start: " + errorCount);
        System.out.println("Log file:           " + AppLogger.LOG_FILE);
    }
}
