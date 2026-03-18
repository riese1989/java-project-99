package hexlet.code.mappers;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderMapper {
    private final PasswordEncoder passwordEncoder;

    @Named("encryptPassword")
    public String encrypt(String password) {
        return password != null ? passwordEncoder.encode(password) : null;
    }
}

