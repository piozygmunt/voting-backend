package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.model.vote.VotingProcess;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotingProcessRepoitory extends JpaRepository<VotingProcess, Long> {
    List<VotingProcess> findAllByStateIsNot(VotingProcess.State state);

    Optional<VotingProcess> findByIdAndStateIsNot(long id, VotingProcess.State state);

}
