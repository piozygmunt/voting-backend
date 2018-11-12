package voting.model.vote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import voting.model.auth.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@ToString
@EqualsAndHashCode
public class VotingInvitation {

    public enum State {
        ACCEPTED, REJECTED, PENDING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User invitedUser;

    @OneToOne
    private VotingProcess votingProcess;

    private State state = State.PENDING;

    private Instant date;


}
