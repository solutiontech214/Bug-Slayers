package com.logger.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {

    // 🔥 Max stack trace length (to avoid huge payloads)
    private static final int MAX_LENGTH = 5000;

    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) return null;

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            throwable.printStackTrace(pw);

            String stackTrace = sw.toString();

            // 🔥 Clean for JSON (important)
            stackTrace = sanitize(stackTrace);

            // 🔥 Limit size (very important for performance)
            if (stackTrace.length() > MAX_LENGTH) {
                stackTrace = stackTrace.substring(0, MAX_LENGTH) + "... [TRUNCATED]";
            }

            return stackTrace;

        } catch (Exception e) {
            return "Failed to parse stack trace";
        }
    }

    // 🔹 Clean string for JSON safety
    private static String sanitize(String input) {
        if (input == null) return null;

        return input
                .replace("\\", "\\\\")   // escape backslashes
                .replace("\"", "'")     // avoid JSON break
                .replace("\n", "\\n")   // keep newlines readable
                .replace("\r", "");     // remove carriage return
    }
}
