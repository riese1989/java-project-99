package hexlet.code.app.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Поле password должно быть заполненным")
    @Size(min = 3, message = "Пароль должен содержать минимум 3 символа")
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
