package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import voting.model.auth.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByUsernameStartingWith(String usernameStart);
}
