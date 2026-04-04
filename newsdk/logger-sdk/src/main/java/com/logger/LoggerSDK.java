package com.logger;

import com.logger.config.SDKConfig;
import com.logger.model.LogEvent;
import com.logger.model.LogLevel;
import com.logger.model.Metadata;
import com.logger.queue.LogQueue;
import com.logger.sender.LogSender;
import com.logger.crash.CrashHandler;
import com.logger.util.StackTraceUtil;

public class LoggerSDK {

    private static SDKConfig config;
    private static boolean initialized = false;

    // 🔥 INIT SDK
    public static void init(SDKConfig sdkConfig) {
        if (initialized) return;

        config = sdkConfig;

        // start background sender (batching)
        //LogSender.start(config.getApiKey(), config.getEndpoint());
        LogSender.start(config);
        
        // setup crash handler
        CrashHandler.init();

        initialized = true;
    }

    // 🔹 INFO
    public static void info(String message) {
        log(message, LogLevel.INFO, null);
    }

    // 🔹 WARN
    public static void warn(String message) {
        log(message, LogLevel.WARN, null);
    }

    // 🔹 ERROR
    public static void error(String message, Throwable throwable) {
        log(message, LogLevel.ERROR, throwable);
    }

    // 🔹 FATAL
    public static void fatal(String message, Throwable throwable) {
        log(message, LogLevel.FATAL, throwable);
    }

    // 🔥 CORE LOG METHOD
    private static void log(String message, LogLevel level, Throwable throwable) {

        if (!initialized) {
            System.out.println("LoggerSDK not initialized!");
            return;
        }

        try {
            // Create metadata
            Metadata metadata = new Metadata(
                    config.getProjectId(),
                    config.getModuleId(),
                    config.getSubModuleId(),
                    config.getEnvironment(),
                    System.currentTimeMillis()
            );

            // Stack trace only for ERROR/FATAL
            String stackTrace = null;
            if (throwable != null && (level == LogLevel.ERROR || level == LogLevel.FATAL)) {
                stackTrace = StackTraceUtil.getStackTrace(throwable);
            }

            // Create log event
            LogEvent event = new LogEvent(
                    message,
                    level,
                    metadata,
                    stackTrace
            );

            // Add to queue (NON-BLOCKING)
            LogQueue.add(event);

        } catch (Exception e) {
            // SDK should NEVER crash user app
            System.out.println("LoggerSDK internal error");
        }
    }

    // 🔹 Optional: update submodule dynamically
    public static void setSubModule(String subModuleId) {
        if (config != null) {
            config.setSubModuleId(subModuleId);
        }
    }
}