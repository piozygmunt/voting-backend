package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import voting.model.auth.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findDistinctFirstByName(String name);
}
