package lk.usj.OPD_Management.java.utils.logger;


import lk.usj.OPD_Management.java.helper.LogContext;
import lk.usj.OPD_Management.java.helper.LogLevel;
import lk.usj.OPD_Management.java.utils.logger.stratergies.AuditLoggingStrategy;
import lk.usj.OPD_Management.java.utils.logger.stratergies.ErrorLoggingStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/// The Singleton Logger class. There will only ever be one instance of this.
public final  class Logger {
    // The single, static instance of the Logger.
    private static Logger instance;

    // Holds the username of the currently logged-in user.
    private String currentUser;

    // Date formatter for consistent timestamps.
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor to prevent anyone else from creating an instance.
    private Logger() {
        // Default user if none is set.
        this.currentUser = "System";
    }

    // Public method to get the single instance of the logger.
    // The 'synchronized' keyword makes it thread-safe.
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // Method to set the user who is performing the actions.
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    // Public method for logging informational (audit) messages.
    public void info(String message) {
        log(new AuditLoggingStrategy(), message, LogLevel.INFO);
    }

    // Public method for logging error messages.
    public void error(String message) {
        log(new ErrorLoggingStrategy(), message, LogLevel.ERROR);
    }

    // Private helper method that uses the strategy pattern.
    private void log(LoggingStrategy strategy, String message, LogLevel level) {
        // 1. Create a context object with all necessary information.
        LogContext context = new LogContext(
                LocalDateTime.now().format(formatter),
                this.currentUser,
                message,
                level
        );

        // 2. Execute the chosen strategy with the context.
        strategy.log(context);
    }
}
