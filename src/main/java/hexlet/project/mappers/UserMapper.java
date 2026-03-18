package hexlet.project.mappers;

import hexlet.project.dtos.requests.UserRequestDto;
import hexlet.project.dtos.response.UserResponseDto;
import hexlet.project.models.User;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(
        uses = { PasswordEncoderMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class UserMapper implements BaseMapper<UserRequestDto, UserResponseDto, User> {
    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")
    @Mapping(target = "role", constant = "ROLE_USER")
    public abstract User toEntity(UserRequestDto dto);

    public abstract UserResponseDto toResponse(User entity);

    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")
    public abstract void updateEntity(UserRequestDto dto, @MappingTarget User entity);
}

