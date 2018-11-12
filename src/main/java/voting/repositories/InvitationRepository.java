package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.model.vote.VotingInvitation;
import voting.model.vote.VotingProcess;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<VotingInvitation, Long> {
    Optional<VotingInvitation> findByVotingProcess(VotingProcess votingProcess);
}
