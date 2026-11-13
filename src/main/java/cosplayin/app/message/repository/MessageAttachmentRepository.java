package cosplayin.app.message.repository;

import cosplayin.app.message.model.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {
}
