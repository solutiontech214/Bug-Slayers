package com.example.demo.repository;

import com.example.demo.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findByAppName(String appName);

    List<Log> findByLevel(String level);

    List<Log> findByAppNameAndLevel(String appName, String level);
    Page<Log> findByAppNameAndLevelAndTimestampBetween(
            String appName,
            String level,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Log> findByTimestampBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

}
