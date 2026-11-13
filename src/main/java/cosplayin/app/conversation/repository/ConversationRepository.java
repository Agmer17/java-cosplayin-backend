package cosplayin.app.conversation.repository;

import cosplayin.app.conversation.model.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findConversationByConvKey(String convKey);
}
