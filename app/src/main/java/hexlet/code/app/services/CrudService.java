package hexlet.code.app.services;

import java.util.List;

public interface CrudService <T, U>{
    T findById(Long id);
    U findByIdEntity(Long id);
    List<T> findAll();
    T create(T dto);
    T update(T dto);
    void delete(Long id);
}
