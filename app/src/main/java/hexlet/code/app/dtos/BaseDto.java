package hexlet.code.app.dtos;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseDto {
    protected Long id;
}
