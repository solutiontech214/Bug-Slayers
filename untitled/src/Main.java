import com.sdk.logging.LoggerSDK;

public class Main {

    public static void main(String[] args) {

        LoggerSDK logger = new LoggerSDK(
                "http://localhost:8080",
                "payment-service",
                "dev",
                "checkout-module",
                "my-secret-key"
        );

        logger.info("App started");

        try {
            int x = 10 / 0; // crash
        } catch (Exception e) {
            logger.error("Something failed", e);
        }

        logger.warn("This is warning");
        logger.debug("Debug message");

        System.out.println("App continues running...");
    }
}