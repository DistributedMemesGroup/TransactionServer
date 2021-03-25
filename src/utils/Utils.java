package utils;

import java.util.Arrays;

public class Utils {
    public static boolean isValidIpAddr(String ipAddr) {
        String[] frags = ipAddr.split("\\.");
        return ipAddr.equals("localhost") || (frags.length == 4 && Arrays.stream(frags).allMatch(Utils::isInt));
    }

    // Checks if input str is an int
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
}
