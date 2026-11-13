package cosplayin.app.conversation.repository;

import cosplayin.app.conversation.model.entity.ConversationParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipants, UUID> {
}
