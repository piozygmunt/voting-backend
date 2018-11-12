package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.model.auth.User;
import voting.model.vote.VotingInvitation;

import java.util.List;

@Repository
public interface VotingInvitationRepository extends JpaRepository<VotingInvitation, Long> {
    List<VotingInvitation> findAllByInvitedUserOrVotingProcessCreatedBy(User invitedUser, User createdBy);
}
