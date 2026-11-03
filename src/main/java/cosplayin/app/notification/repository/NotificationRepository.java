package cosplayin.app.notification.repository;

import cosplayin.app.notification.model.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notifications, UUID> {

    Optional<Notifications> findByGroupKey(String groupKey);


    @Modifying
    @Query("""
    UPDATE Notifications n
    SET n.readAt = :now
    WHERE n.recipient.id = :recipientId
      AND (
          n.readAt IS NULL
          OR n.lastActivityAt < :now
      )
    """)
    void markAsRead(
            @Param("recipientId") UUID recipientId,
            @Param("now") LocalDateTime now
    );


}
