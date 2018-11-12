package voting.model.vote;

import lombok.Data;
import voting.model.auth.User;

@Data
public class VoteResponse {
    private VotingProcess.State newState;
    private long votedProcId;
    private Pick pickMade;
    private User nextUser;
}
