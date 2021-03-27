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
    
    //Create a log message for output
    public void logInfo(String message) {
        logLine(message, System.out);
    }
    
    //Create an error log output with given string
    public void logError(String errText) {
        logLine(errText, System.err);
    }
    
    //Create an error log output with given error
    public void logError(Exception err) {
        logLine(err.getMessage(), System.err);
    }
    
    //Format for log messages 
    private void logLine(String message, PrintStream stream) {
        //get current time
        var dateTime = LocalDateTime.now();
        String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        //print to console 
        stream.printf("# %s # %s\n", formattedDate, message);
    }

}
