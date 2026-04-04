package com.logger.sender;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public static void send(String jsonPayload, String apiKey, String endpoint) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);

            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            // Optional debug
            if (responseCode >= 400) {
                System.out.println("Log send failed: " + responseCode);
            }

        } catch (Exception e) {
            // 🔥 Fail silently (VERY IMPORTANT)
            System.out.println("Failed to send logs (network issue)");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}