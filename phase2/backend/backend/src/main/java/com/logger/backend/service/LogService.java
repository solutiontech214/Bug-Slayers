package com.logger.backend.service;

import com.logger.backend.dto.LogRequest;
import com.logger.backend.model.LogEntity;
import com.logger.backend.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public void saveLog(LogRequest request) {

        LogEntity entity = new LogEntity();
        entity.setMessage(request.getMessage());
        entity.setLevel(request.getLevel());
        entity.setTimestamp(request.getTimestamp());

        repository.save(entity);
    }
}