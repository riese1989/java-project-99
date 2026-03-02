package hexlet.code.app.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TaskDto extends BaseDto {
    private String name;
    private Integer index;
    private String description;
    private TaskStatusDto taskStatus;
    private UserDto assignee;
    private LocalDateTime createdAt;
}
