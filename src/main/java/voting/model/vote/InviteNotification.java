package voting.model.vote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import voting.model.common.Notification;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class InviteNotification extends Notification {
    @OneToOne
    private VotingInvitation votingInvitation;
}
