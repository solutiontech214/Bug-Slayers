

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LogService {

    public void processLogs(List<Map<String, Object>> logs, String apiKey) {
        System.out.println("Logs received: " + logs.size());
    }
}