package voting.model.vote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import voting.model.auth.User;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode
@ToString
public class Pick{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private Action action;

    private long orderIndex;

    @ManyToOne
    private User user;

    @ManyToOne
    private PickingItem pickingItem;
}
