package voting.model.chat;

import lombok.Data;
import lombok.ToString;
import voting.model.auth.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User user;

    private String content;

    private Instant timestamp;
}
