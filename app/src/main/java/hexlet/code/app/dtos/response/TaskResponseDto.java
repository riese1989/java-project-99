package hexlet.code.app.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TaskResponseDto extends BaseResponseDto {
    private Integer index;
    private LocalDateTime createdAt;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
