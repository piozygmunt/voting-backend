package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.model.vote.Pick;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {
}
