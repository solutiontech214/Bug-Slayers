package com.logger.backend.repository;

import com.logger.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByProjectId(Long projectId);

    // Get all teams a user belongs to
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :userId")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);
}
