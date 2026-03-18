package hexlet.code.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto extends BaseRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
