package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

// One shared logger that writes to logs/app.log, so the console menu stays clean.
public final class AppLogger {

    public static final Path LOG_FILE = Path.of("logs", "app.log");

    private static final Logger LOGGER = create();

    private AppLogger() {
    }

    public static Logger get() {
        return LOGGER;
    }

    private static Logger create() {
        Logger logger = Logger.getLogger("ProjectHub");
        logger.setUseParentHandlers(false);

        try {
            Files.createDirectories(LOG_FILE.getParent());
            FileHandler handler = new FileHandler(LOG_FILE.toString(), true);
            handler.setFormatter(new LineFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            // Without a log file the app still works: logs then go to the console.
            logger.setUseParentHandlers(true);
            System.err.println("Warning: could not open " + LOG_FILE + ", logging to console.");
        }
        return logger;
    }

    // Example line: 2026-10-19 07:05:12 INFO    Project created: PRJ006 "Robot Arm" by Admin
    private static class LineFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String line = String.format("%1$tF %1$tT %2$-7s %3$s%n",
                    record.getMillis(), record.getLevel(), formatMessage(record));

            if (record.getThrown() != null) {
                StringWriter trace = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(trace));
                line += trace;
            }
            return line;
        }
    }
}
