package hexlet.project.repositories;

import hexlet.project.models.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
        SELECT t FROM Task t
        WHERE (:titleCont IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :titleCont, '%')))
          AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
          AND (:status IS NULL OR t.taskStatus.slug = :status)
          AND (:labelId IS NULL OR :labelId IN (SELECT l.id FROM t.labels l))
    """)
    List<Task> findByFilter(
            @Param("titleCont") String titleCont,
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status,
            @Param("labelId") Long labelId
    );

    @EntityGraph(attributePaths = {"labels", "taskStatus", "assignee"})
    Optional<Task> findById(Long id);
}
