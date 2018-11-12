package voting.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import voting.model.auth.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@ToString
@EqualsAndHashCode(exclude = {"user", "type", "message"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Notification {

    public enum State {
        SEEN, UNSEEN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public enum Type {
        INVITE, INFO
    }
    private Type type;
    private State state;
    private String message;
    private Instant createdAt;
    private Instant seenAt;
    private boolean ackRequired;
    @ManyToOne
    private User user;
}
