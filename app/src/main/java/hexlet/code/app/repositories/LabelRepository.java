package hexlet.code.app.repositories;

import hexlet.code.app.models.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findLabelByName(String name);
    @Query("SELECT t.labels FROM Task t WHERE t.id = :taskId")
    Optional<Set<Label>> findLabelsByTaskId(@Param("taskId") Long taskId);
}
