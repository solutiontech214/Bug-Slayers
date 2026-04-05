package com.logger.crash;

import com.logger.LoggerSDK;
import com.logger.sender.LogSender;

public class CrashHandler {

    private static boolean initialized = false;

    // 🔥 Initialize crash handler
    public static void init() {
        if (initialized) return;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                // 🔥 Send crash as FATAL log
                LoggerSDK.fatal("Unhandled exception in thread: " + thread.getName(), throwable);

                // 🔥 Force synchronous flush before JVM dies
                LogSender.flushNow();

            } catch (Exception e) {
                // Avoid recursive crash
                System.out.println("CrashHandler failed");
            }
        });

        initialized = true;
    }
}
