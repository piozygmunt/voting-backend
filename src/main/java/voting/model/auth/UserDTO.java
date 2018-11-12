package voting.model.auth;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class UserDTO {
    private long id;
    private String username;
    private String email;
}
