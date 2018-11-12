package voting.model.chat;

import lombok.Data;

import java.time.Instant;

@Data
public class MessageDTO {
    private long id;
    private String content;
    private Instant timestamp;
    private String username;
}
