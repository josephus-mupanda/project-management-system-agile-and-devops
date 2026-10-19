package utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppLoggerTest {

    @Test
    void messagesAreWrittenToTheLogFile() throws IOException {
        String message = "test message " + System.nanoTime();

        AppLogger.get().info(message);

        assertTrue(Files.exists(AppLogger.LOG_FILE));
        String content = Files.readString(AppLogger.LOG_FILE);
        assertTrue(content.contains("INFO"));
        assertTrue(content.contains(message));
    }
}
