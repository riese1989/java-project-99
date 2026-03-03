package hexlet.code.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends BaseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
