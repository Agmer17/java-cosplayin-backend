package cosplayin.app.conversation.service;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.dto.CreateConversationGroupDTO;
import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.conversation.model.entity.ConversationParticipants;
import cosplayin.app.conversation.model.type.ConversationRoles;
import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.conversation.repository.ConversationJdbcQuery;
import cosplayin.app.conversation.repository.ConversationParticipantRepository;
import cosplayin.app.conversation.repository.ConversationRepository;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.service.UsersService;
import cosplayin.app.utils.storage.StorageUtils;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private final ConversationRepository conversationRepo;
    private final ConversationParticipantRepository participantRepo;
    private final ConversationJdbcQuery conversationQuery;
    private final StorageUtils storageUtils;
    private final UsersService userService;

    @Transactional
    public Conversation getOrCreateConversation(String convKey, ConversationType type, UUID creator, UUID target) {
        return conversationRepo.findConversationByConvKey(convKey).orElseGet(() -> {

            Users creatorUsers = Users.builder().id(creator).build();
            Users targetUsers = Users.builder().id(target).build();

            Conversation conv = conversationRepo.save(
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

    @Transactional
    public Conversation createConversationGroup(UUID curr, CreateConversationGroupDTO dto) {

        if (dto.getInitialMember() == null) {
            throw new RequestValidationException("you need at least 2 member to creating a group chat");
        }

        if (dto.getInitialMember().isEmpty()) {
            throw new RequestValidationException("you need at least 2 member to creating a group chat");
        }

        userService.validateUsersExist(dto.getInitialMember());

        Users creator = Users.builder().id(curr).build();

        UUID convId = UUID.randomUUID();
        String key = this.generateGroupChatKey(convId);

        Conversation conversation = Conversation.builder()
                .id(convId)
                .convKey(key)
                .conversationType(ConversationType.GROUP)
                .name(dto.getGroupName())
                .createdBy(creator)
                .build();

        List<ConversationParticipants> participants = dto.getInitialMember().stream()
                .map(u -> ConversationParticipants.builder()
                        .user(Users.builder().id(u).build())
                        .conversationRole(ConversationRoles.USER)
                        .conversation(conversation)
                        .build()
                )
                .collect(Collectors.toCollection(ArrayList::new));

        if (dto.getGroupImage() != null) {
            FileValidationPolicy filePolicy = new FileValidationPolicy(Set.of(SupportedFileType.IMAGE), MAX_FILE_SIZE);
            FileModel saved = storageUtils.savePublicFile(dto.getGroupImage(), filePolicy, "group", "message", "picture");
            conversation.setConversationPic(this.storageUtils.signPublicFileUrl(saved.getFilePath()));
        }

        conversationRepo.saveAndFlush(conversation);
        participants.add(ConversationParticipants.builder()
                .user(creator)
                .conversationRole(ConversationRoles.ADMIN)
                .conversation(conversation).build());

        participantRepo.saveAllAndFlush(participants);
        return conversation;
    }

    public Optional<Conversation> getByConversationKey(String key) {
        return conversationRepo.findConversationByConvKey(key);
    }

    public List<ConversationResponseDTO> getLatestMessage(UUID userId) {
        return conversationQuery.findByUser(userId);
    }

    public ConversationResponseDTO getConversationGroupByKey(String key, UUID curr) {
        return conversationQuery.findByConvKey(key, curr).orElseThrow(() -> new NotFoundException("conversation was not found"));
    }

    public void validateGroupMember(UUID groupId, UUID curr, ConversationResponseDTO groupResp) {
        String key = this.generateGroupChatKey(groupId);
        boolean isMember = groupResp.getParticipants()
                .stream()
                .anyMatch(participant -> participant.getId().equals(curr));

        if (!isMember) {
            throw new ForbiddenAccessExceptions("you can;t send message to this group");
        }
    }

    public String generateGroupChatKey(UUID groupId) {
        return ConversationType.GROUP.toString() + ":" + groupId.toString();
    }
}
