package hexlet.code.app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends BaseDto {
    private String name;
    private Integer index;
    private String description;
    @JsonIgnore
    private TaskStatusDto taskStatus;
    @JsonIgnore
    private UserDto assignee;
    @JsonIgnore
    private Set<LabelDto> labels;

    @JsonProperty("assignee_id")
    public void setAssigneeId(Long id) {
        if (id != null) {
            this.assignee = UserDto.builder().id(id).build();
        }
    }

    @JsonProperty("status")
    public void setStatusSlug(String slug) {
        if (slug != null) {
            this.taskStatus = TaskStatusDto.builder().slug(slug).build();
        }
    }

    @JsonProperty("taskLabelIds")
    public void setLabelIds(Set<Long> ids) {
        if (ids != null) {
            this.labels = ids.stream()
                    .map(id -> LabelDto.builder().id(id).build())
                    .collect(Collectors.toSet());
        }
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.name = title;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.description = content;
    }

    @JsonProperty("assignee_id")
    public Long getAssigneeId() {
        return assignee != null ? assignee.getId() : null;
    }

    @JsonProperty("status")
    public String getStatusSlug() {
        return taskStatus != null ? taskStatus.getSlug() : null;
    }

    @JsonProperty("taskLabelIds")
    public Set<Long> getLabelIds() {
        return labels != null
                ? labels.stream().map(LabelDto::getId).collect(Collectors.toSet())
                : null;
    }


    private LocalDateTime createdAt;

}
