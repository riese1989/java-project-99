package hexlet.code.app.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDto extends BaseRequestDto {
    @JsonProperty("titleCont")
    private String title;

    private Long assigneeId;

    @JsonProperty("status")
    private String slug;

    private Long labelId;
}
