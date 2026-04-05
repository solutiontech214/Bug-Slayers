package com.repository;

import com.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {

    // 🔹 Find logs by project
    List<LogEntity> findByProjectId(String projectId);

    // 🔹 Find logs by level (INFO, ERROR, etc.)
    List<LogEntity> findByLevel(String level);

    // 🔹 Find logs by module
    List<LogEntity> findByModuleId(String moduleId);

    // 🔹 Find logs by environment
    List<LogEntity> findByEnvironment(String environment);
}