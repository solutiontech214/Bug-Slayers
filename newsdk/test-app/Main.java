import com.logger.LoggerSDK;
import com.logger.config.SDKConfig;

public class Main {

    public static void main(String[] args) {

        //  1. INIT CONFIG
        SDKConfig config = new SDKConfig.Builder()
                .apiKey("sk_test_123")
                .projectId("payment-service")
                .moduleId("auth-module")
                .subModuleId("login-api")
                .environment("development")
                .endpoint("http://localhost:8080/logs/batch")
                .batchSize(5)
                .flushIntervalSeconds(3)
                .build();

        LoggerSDK.init(config);

        //  2. BASIC LOGS
        LoggerSDK.info("App started ");
        LoggerSDK.warn("This is a warning ");

        //  3. ERROR LOG
        try {
            int x = 10 / 0;
        } catch (Exception e) {
            LoggerSDK.error("Divide by zero error", e);
        }

        //  4. CHANGE SUBMODULE (DYNAMIC)
        LoggerSDK.setSubModule("payment-api");
        LoggerSDK.info("Switched to payment module");

        //  5. MULTIPLE LOGS (BATCH TEST)
        for (int i = 1; i <= 10; i++) {
            LoggerSDK.info("Batch log " + i);
        }

        //  6. FORCE CRASH (TEST CRASH HANDLER)
        // Uncomment to test crash
        // int crash = 10 / 0;

        // Keep app alive to allow async send
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}