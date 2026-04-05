package com.logger.backend.repository;

import com.logger.backend.model.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {
    List<SubModule> findByTeamId(Long teamId);
    List<SubModule> findByProjectId(Long projectId);

    // Get all subModuleIds for a developer (via their teams)
    @Query("""
        SELECT sm.moduleId FROM SubModule sm
        WHERE sm.team.id IN (
            SELECT t.id FROM Team t JOIN t.members m WHERE m.id = :userId
        )
    """)
    List<String> findSubModuleIdsByUserId(@Param("userId") Long userId);
}
