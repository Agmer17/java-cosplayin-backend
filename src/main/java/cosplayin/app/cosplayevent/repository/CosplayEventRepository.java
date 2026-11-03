package cosplayin.app.cosplayevent.repository;


import cosplayin.app.cosplayevent.model.entity.CosplayEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CosplayEventRepository extends JpaRepository<CosplayEvents, UUID> {
}
