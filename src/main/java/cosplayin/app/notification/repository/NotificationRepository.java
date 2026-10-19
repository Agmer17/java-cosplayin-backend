package cosplayin.app.notification.repository;

import cosplayin.app.notification.model.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notifications, UUID> {

    Optional<Notifications> findByGroupKey(String groupKey);



}
