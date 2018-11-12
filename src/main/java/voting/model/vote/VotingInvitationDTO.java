package voting.model.vote;

import lombok.Data;
import voting.model.auth.UserDTO;

import java.time.Instant;

@Data
public class VotingInvitationDTO {
    private long id;

    private UserDTO invitedUser;

    private VotingProcessDTO votingProcess;

    private VotingInvitation.State state;

    private Instant date;

}
