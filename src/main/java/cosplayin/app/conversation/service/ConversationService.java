package cosplayin.app.conversation.service;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.conversation.model.entity.ConversationParticipants;
import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.conversation.repository.ConversationJdbcQuery;
import cosplayin.app.conversation.repository.ConversationParticipantRepository;
import cosplayin.app.conversation.repository.ConversationRepository;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepo;
    private final ConversationParticipantRepository participantRepo;
    private final ConversationJdbcQuery conversationQuery;

    @Transactional
    public Conversation getOrCreateConversation(String convKey, ConversationType type, UUID creator) {
        return conversationRepo.findConversationByConvKey(convKey).orElseGet(() -> {
            return conversationRepo.saveAndFlush(
                    Conversation.builder()
                    .convKey(convKey)
                    .conversationType(type)
                    .createdBy(Users.builder().id(creator).build())
                    .build());
        });

    }
    @Transactional
    public Conversation getOrCreateConversation(String convKey, ConversationType type, UUID creator, UUID target) {
        return conversationRepo.findConversationByConvKey(convKey).orElseGet(() -> {

            Users creatorUsers = Users.builder().id(creator).build();
            Users targetUsers = Users.builder().id(target).build();

            Conversation  conv = conversationRepo.save(
                    Conversation.builder()
                    .convKey(convKey)
                    .conversationType(type)
                    .createdBy(creatorUsers)
                    .build());

            List<ConversationParticipants> participants = new ArrayList<>(2);
            participants.add(ConversationParticipants.builder().user(creatorUsers).conversation(conv).build());
            participants.add(ConversationParticipants.builder().user(targetUsers).conversation(conv).build());

            participantRepo.saveAll(participants);
            return conv;
        });

    }

    public Optional<Conversation> getByConversationKey(String key) {
        return conversationRepo.findConversationByConvKey(key);
    }

    public List<ConversationResponseDTO> getLatestMessage(UUID userId) {
        return conversationQuery.findByUser(userId);
    }
}
