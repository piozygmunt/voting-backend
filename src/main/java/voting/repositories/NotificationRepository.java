package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.model.auth.User;
import voting.model.common.Notification;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository<T extends Notification> extends JpaRepository<T, Long> {
    List<T> findAllByUserAndStateAndAndCreatedAtBefore(User user, Notification.State state, Instant before);
}
