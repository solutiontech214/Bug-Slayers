package com.logger.backend.repository;

import com.logger.backend.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<LogEntity, Long> {

    List<LogEntity> findByProjectId(String projectId);

    List<LogEntity> findByModuleId(String moduleId);
    List<LogEntity> findByProjectIdAndModuleId(String projectId, String moduleId);
}