package hexlet.code.app.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LabelDto extends BaseDto {
    private String name;
    private Set<TaskDto> tasks;
    private LocalDateTime createdAt;
}
