package hexlet.code.app.services;

import java.util.List;

public interface CrudService <T>{
    T findById(Long id);
    List<T> findAll();
    T create(T dto);
    T update(T dto);
    void delete(Long id);
}
