package hexlet.code.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends BaseDto {
    private String name;
    private Integer index;
    private String description;
    private TaskStatusDto taskStatus;
    private UserDto assignee;
    private Set<LabelDto> labels;
    private LocalDateTime createdAt;
}
