package voting.model.vote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import voting.model.auth.User;
import voting.model.chat.Message;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class VotingProcess {

    public String actionString;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private long id;
    @Enumerated(EnumType.STRING)
    private State state = State.WAITING;
    private int currentStep = 0;
    @ManyToOne
    private User createdBy;
    @ManyToMany
    @OrderColumn
    private List<User> users;
    @ManyToMany
    @OrderColumn
    private List<Pick> picks = new ArrayList<>();
    @ManyToMany
    @OrderColumn
    private List<PickingItem> possibilities;
    @OneToMany
    @OrderBy("timestamp DESC")
    private List<Message> messages;

    public User getCurrentUser() {
        return users.get(currentStep % users.size());
    }

    public void incrementStep() {
        ++currentStep;
    }

    public enum State {
        WAITING, STARTED, FINISHED
    }
}
