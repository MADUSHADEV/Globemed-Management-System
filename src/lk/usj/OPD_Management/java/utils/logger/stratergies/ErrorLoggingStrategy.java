package lk.usj.OPD_Management.java.utils.logger.stratergies;

import lk.usj.OPD_Management.java.helper.LogContext;
import lk.usj.OPD_Management.java.utils.logger.LoggingStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Concrete strategy for writing error logs to a file.
public class ErrorLoggingStrategy implements LoggingStrategy {
    private static final String ERROR_FILE_PATH = "logs/error/error.log";

    @Override
    public void log(LogContext context) {
        // Ensure the directory exists
        File logFile = new File(ERROR_FILE_PATH);
        logFile.getParentFile().mkdirs();

        // Use try-with-resources for safety.
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String logEntry = String.format("[%s] [%s] [User: %s]: %s",
                    context.getTimestamp(),
                    context.getLevel(),
                    context.getUser(),
                    context.getMessage()
            );
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to error log file: " + e.getMessage());
        }
    }
}
