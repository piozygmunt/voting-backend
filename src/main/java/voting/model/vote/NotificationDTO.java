package voting.model.vote;

import lombok.Data;
import voting.model.auth.User;
import voting.model.auth.UserDTO;
import voting.model.common.Notification;

import javax.persistence.ManyToOne;
import java.time.Instant;

@Data
public class NotificationDTO {
    private long id;

    private Notification.Type type;
    private Notification.State state;
    private String message;
    private Instant createdAt;
    private Instant seenAt;
    private boolean ackRequired;
    private UserDTO user;
}
