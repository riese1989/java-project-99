package hexlet.code.app.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto extends BaseRequestDto {
    private Integer index;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String title;
    private String content;
    @JsonProperty("status")
    private String slug;
    private Set<LabelRequestDto> labels;
}
