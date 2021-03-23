package logger;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger instance = null;

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void logInfo(String message) {
        logLine(message, System.out);
    }

    public void logError(String errText) {
        logLine(errText, System.err);
    }

    public void logError(Exception err) {
        logLine(err.getMessage(), System.err);
    }

    private void logLine(String message, PrintStream stream) {
        var dateTime = LocalDateTime.now();
        String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        stream.printf("# %s # %s\n", formattedDate, message);
    }

}
