package voting.model.vote;

import lombok.Data;
import voting.model.auth.UserDTO;

@Data
public class PickDTO {
    private long id;

    private Action action;

    private long orderIndex;

    private UserDTO user;

    private PickingItem pickingItem;
}
