package lk.usj.OPD_Management.java.helper;

// A simple data object to pass all log-related info to the strategy.
public class LogContext {
    private final String timestamp;
    private final String user;
    private final String message;
    private final LogLevel level;

    public LogContext(String timestamp, String user, String message, LogLevel level) {
        this.timestamp = timestamp;
        this.user = user;
        this.message = message;
        this.level = level;
    }

    // Getters for all fields
    public String getTimestamp() { return timestamp; }
    public String getUser() { return user; }
    public String getMessage() { return message; }
    public LogLevel getLevel() { return level; }
}
