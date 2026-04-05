package com.logger.backend.repository;

import com.logger.backend.model.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogRepository extends JpaRepository<LogEntity, Long> {

    // Filtered by level
    Page<LogEntity> findByLevel(String level, Pageable pageable);

    // Scoped to specific subModuleId (DEVELOPER data isolation)
    Page<LogEntity> findBySubModuleId(String subModuleId, Pageable pageable);

    // Scoped to a list of subModuleIds (developer's teams)
    Page<LogEntity> findBySubModuleIdIn(List<String> subModuleIds, Pageable pageable);

    // Scoped to project (PROJECT_MANAGER access)
    Page<LogEntity> findByProjectId(String projectId, Pageable pageable);

    // All logs with pagination (GLOBAL_ADMIN)
    Page<LogEntity> findAll(Pageable pageable);

    // PostgreSQL Full-Text Search using GIN index
    @Query(value = """
        SELECT * FROM logs
        WHERE search_vector @@ plainto_tsquery('english', :query)
        AND (:subModuleId IS NULL OR sub_module_id = :subModuleId)
        ORDER BY timestamp DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<LogEntity> fullTextSearch(
        @Param("query") String query,
        @Param("subModuleId") String subModuleId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    // Count by level per project
    @Query("SELECT l.level, COUNT(l) FROM LogEntity l WHERE l.projectId = :projectId GROUP BY l.level")
    List<Object[]> countByLevelForProject(@Param("projectId") String projectId);

    // Count by level (global)
    @Query("SELECT l.level, COUNT(l) FROM LogEntity l GROUP BY l.level")
    List<Object[]> countByLevel();

    // Update search vector after insert (called from service)
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE logs SET search_vector = to_tsvector('english', coalesce(message, ''))
        WHERE id = :id
        """, nativeQuery = true)
    void updateSearchVector(@Param("id") Long id);
}