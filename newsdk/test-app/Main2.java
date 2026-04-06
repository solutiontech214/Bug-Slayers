import com.logger.LoggerSDK;
import com.logger.config.SDKConfig;

public class Main2 {

    public static void main(String[] args) {

        // 1. INIT CONFIG (Different Data)
        SDKConfig config = new SDKConfig.Builder()
                .apiKey("sk_live_789")
                .projectId("order-service")
                .moduleId("checkout-module")
                .subModuleId("order-create-api")
                .environment("production")
                .endpoint("http://localhost:8080/logs/batch")
                .batchSize(3)
                .flushIntervalSeconds(2)
                .build();

        LoggerSDK.init(config);

        // 2. START LOGS
        LoggerSDK.info("Order service initialized");


        // 3. SIMULATE ORDER PROCESS
        try {
            int orderAmount = 500;
            int discount = 50;
            int finalAmount = orderAmount - discount;

            LoggerSDK.info("Order calculated: ₹" + finalAmount);

            if (finalAmount < 0) {
                throw new RuntimeException("Invalid order amount");
            }

        } catch (Exception e) {
            LoggerSDK.error("Order processing failed", e);
        }

        // 4. SWITCH MODULE (Dynamic Context)
        LoggerSDK.setSubModule("payment-gateway");
        LoggerSDK.info("Redirecting to payment gateway");

        // 5. PAYMENT SIMULATION
        try {
            String paymentStatus = "FAILED";

            if (paymentStatus.equals("FAILED")) {
                throw new Exception("Payment declined");
            }

            LoggerSDK.info("Payment successful");

        } catch (Exception e) {
            LoggerSDK.error("Payment error occurred", e);
        }

        // 6. BATCH LOG TEST
        for (int i = 1; i <= 6; i++) {
            LoggerSDK.info("Order batch log " + i);
        }

        // 7. WAIT FOR ASYNC LOG FLUSH
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            LoggerSDK.error("Thread interrupted", e);
        }
    }
}