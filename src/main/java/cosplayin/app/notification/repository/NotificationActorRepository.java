package cosplayin.app.notification.repository;

import cosplayin.app.notification.model.entity.NotificationActor;
import cosplayin.app.notification.model.entity.NotificationActorId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationActorRepository extends JpaRepository<NotificationActor, NotificationActorId> {


}
