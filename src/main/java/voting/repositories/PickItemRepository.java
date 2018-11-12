package voting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import voting.model.vote.PickingItem;

import java.util.List;
import java.util.Optional;

public interface PickItemRepository extends JpaRepository<PickingItem, Long> {
    List<PickingItem> findAllByNameStartingWith(String query);
    Optional<PickingItem> findByName(String name);
    Optional<PickingItem> findByImgUrl(String imgUrl);
}
