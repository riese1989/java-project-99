package hexlet.code.app.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UserDto extends BaseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    @Column(nullable = false)
    @NotNull(message = "Поле password должно быть заполненным")
    @Size(min = 3, message = "Пароль должен содержать минимум 3 символа")
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
