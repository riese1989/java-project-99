package hexlet.code.app.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String name;
    private Integer index;
    private String description;
    private TaskStatusDto taskStatus;
    private UserDto assignee;
    private LocalDateTime createdAt;
}
