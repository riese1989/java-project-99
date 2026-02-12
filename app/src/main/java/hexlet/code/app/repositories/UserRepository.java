package hexlet.code.app.repositories;

import hexlet.code.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUserById(Long id);
    List<User> findUsersByEmail(String email);
}
