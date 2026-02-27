package hexlet.code.app.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskStatusDto {
    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}
