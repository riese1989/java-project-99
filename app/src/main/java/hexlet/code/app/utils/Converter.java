package hexlet.code.app.utils;

public interface Converter <T, U> {
    U convertToEntity(T dto);
    T convertToDto(U entity);
}
