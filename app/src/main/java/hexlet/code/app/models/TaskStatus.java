package hexlet.code.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_statuses")
@Data
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Поле name должно быть заполненным")
    private String name;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Поле slug должно быть заполненным")
    private String slug;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
