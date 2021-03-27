package logger;

import java.io.File;
import java.io.IOException;
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

    private PrintStream outStream = System.out;
    private PrintStream errStream = System.err;

    public void activateFileMode() {
        var outFile = new File("info.log");
        var errorFile = new File("err.log");
        try {
            outFile.createNewFile();
            errorFile.createNewFile();
            outStream = new PrintStream(outFile);
            errStream = new PrintStream(errorFile);
        } catch (IOException e) {
            System.err.println("Logger could not be set to log to files rather thatn stdout and stderr");
        }

    }

    // Create a log message for output
    public void logInfo(String message) {
        logLine(message, outStream);
    }

    public void logInfo(int transactionId, String message) {
        logLine(transactionId, message, outStream);
    }

    // Create an error log output with given string
    public void logError(String errText) {
        logLine(errText, errStream);
    }

    // Create an error log output with given error
    public void logError(Exception err) {
        logLine(err.getMessage(), errStream);
    }

    public void logError(int transactionId, String errText) {
        logLine(transactionId, errText, errStream);
    }

    public void logError(int transactionId, Exception err) {
        logLine(transactionId, err.getMessage(), errStream);
    }

    // Format for log messages
    private void logLine(String message, PrintStream stream) {
        // get current time
        var dateTime = LocalDateTime.now();
        String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        // print to console
        stream.printf("# %s # %s\n", formattedDate, message);
    }

    private void logLine(int transactionId, String message, PrintStream stream) {
        logLine(String.format("Transaction %d: %s", transactionId, message), stream);
    }

}
