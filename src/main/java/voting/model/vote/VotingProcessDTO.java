package voting.model.vote;

import lombok.Data;
import lombok.ToString;
import voting.model.auth.User;
import voting.model.auth.UserDTO;
import voting.model.chat.MessageDTO;

import javax.persistence.*;
import java.util.List;

@Data
@ToString

public class VotingProcessDTO {

    private String actionString;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private VotingProcess.State state;

    private int currentStep;

    private UserDTO currentUser;


    private List<UserDTO> users;


    private UserDTO createdBy;


    private List<PickDTO> picks;

    private List<PickingItem> possibilities;

    private VotingInvitation votingInvitation;

    private List<MessageDTO> messages;
}
