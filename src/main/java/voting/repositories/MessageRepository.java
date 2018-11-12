package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import voting.model.chat.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
