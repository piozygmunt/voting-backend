package voting.model.vote;

import lombok.Data;

@Data
public class InviteNotificationDTO extends NotificationDTO {
    private VotingInvitationDTO votingInvitation;

}
