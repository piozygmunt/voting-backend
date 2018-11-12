package voting.model.vote;

import lombok.Data;
import voting.model.auth.UserDTO;

import java.util.List;

@Data
public class VotingProcessRequest {
    private String actionString;
    private List<PickingItem> items;
    private UserDTO user;
}
