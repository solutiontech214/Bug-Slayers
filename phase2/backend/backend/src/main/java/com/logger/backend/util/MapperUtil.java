package com.logger.backend.util;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.model.LogEntity;

import java.util.Map;

public class MapperUtil {

    public static LogEntity toEntity(LogRequest log, String apiKey) {

        Map<String, Object> metadata = log.getMetadata();

        String projectId = metadata != null ? (String) metadata.get("projectId") : null;
        String moduleId = metadata != null ? (String) metadata.get("moduleId") : null;
        String subModuleId = metadata != null ? (String) metadata.get("subModuleId") : null;
        String environment = metadata != null ? (String) metadata.get("environment") : null;

        Long timestamp = null;
        if (metadata != null && metadata.get("timestamp") != null) {
            timestamp = ((Number) metadata.get("timestamp")).longValue();
        }

        LogEntity entity = new LogEntity();

        entity.setMessage(log.getMessage());
        entity.setLevel(log.getLevel());
        entity.setStackTrace(log.getStackTrace());

        entity.setProjectId(projectId);
        entity.setModuleId(moduleId);
        entity.setSubModuleId(subModuleId);
        entity.setEnvironment(environment);
        entity.setTimestamp(timestamp);

        return entity;
    }
}