package cosplayin.app.message.service;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.dto.ParticipantResponse;
import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.conversation.service.ConversationService;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.message.event.GroupMessageCreatedEvent;
import cosplayin.app.message.event.PrivateMessageCreatedEvent;
import cosplayin.app.message.model.dto.CreateMessageDTO;
import cosplayin.app.message.model.dto.MessageResponseDTO;
import cosplayin.app.message.model.entity.Message;
import cosplayin.app.message.model.entity.MessageAttachment;
import cosplayin.app.message.repository.MessageAttachmentRepository;
import cosplayin.app.message.repository.MessageJdbcQuery;
import cosplayin.app.message.repository.MessageRepository;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.utils.SignerUrlUtils;
import cosplayin.app.utils.StringUtils;
import cosplayin.app.utils.storage.StorageUtils;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class MessageService {

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;
    private static final Duration MEDIA_URL_EXPIRATION = Duration.ofHours(1);

    private static final FileValidationPolicy MEDIA_POLICY =
            new FileValidationPolicy(
                    Set.of(
                            SupportedFileType.IMAGE,
                            SupportedFileType.VIDEO,
                            SupportedFileType.DOCUMENT
                    ),
                    MAX_FILE_SIZE
            );

    private final MessageRepository messageRepo;
    private final MessageAttachmentRepository attachmentRepo;
    private final ConversationService conversationService;
    private final MessageJdbcQuery messageJdbcQuery;
    private final ApplicationEventPublisher eventPublisher;
    private final StorageUtils storage;
    private final SignerUrlUtils signer;
    private final EntityManager entityManager;

    @Value("${storage.s3.bucket.private}")
    private String privateBucket;

    @Transactional(rollbackFor = Exception.class)
    public MessageResponseDTO createPrivateMessage(
            UUID target,
            UUID curr,
            CreateMessageDTO dto
    ) {
        validateMessageContent(dto);

        Conversation conversation = conversationService.getOrCreateConversation(
                StringUtils.generateConvKey(curr, target),
                ConversationType.PRIVATE,
                curr,
                target
        );

        MessageResponseDTO response = saveMessage(conversation, curr, dto);

        eventPublisher.publishEvent(
                new PrivateMessageCreatedEvent(curr, target, response)
        );

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResponseDTO createGroupMessage(
            UUID groupId,
            UUID curr,
            CreateMessageDTO dto
    ) {
        validateMessageContent(dto);

        String key = conversationService.generateGroupChatKey(groupId);

        ConversationResponseDTO groupResponse =
                conversationService.getConversationGroupByKey(key, curr);

        conversationService.validateGroupMember(groupId, curr, groupResponse);

        Conversation conversation = Conversation.builder()
                .id(groupResponse.getId())
                .build();

        List<UUID> members = groupResponse.getParticipants()
                .stream()
                .map(ParticipantResponse::getId)
                .toList();


        MessageResponseDTO response = saveMessage(conversation, curr, dto);


        eventPublisher.publishEvent(
                new GroupMessageCreatedEvent(members, response)
        );

        return response;
    }

    public List<MessageResponseDTO> getPrivateMessage(
            UUID curr,
            UUID target
    ) {
        String key = StringUtils.generateConvKey(curr, target);

        Optional<Conversation> conversation =
                conversationService.getByConversationKey(key);

        if (conversation.isEmpty()) {
            return new ArrayList<>();
        }

        List<MessageResponseDTO> messages =
                messageJdbcQuery.findByConversation(conversation.get().getId());

        signMediaUrls(messages);

        return messages;
    }

    public List<MessageResponseDTO> getGroupChatMessage(UUID groupId, UUID curr) {
        ConversationResponseDTO groupResp = conversationService.getConversationGroupByKey(
                conversationService.generateGroupChatKey(groupId), curr);

        conversationService.validateGroupMember(groupId, curr, groupResp);

        List<MessageResponseDTO> messages = messageJdbcQuery.findByConversation(groupResp.getId());
        signMediaUrls(messages);
        
        return messages;
    }

    private MessageResponseDTO saveMessage(
            Conversation conversation,
            UUID senderId,
            CreateMessageDTO dto
    ) {
        Message message = Message.builder()
                .conversation(conversation)
                .sender(Users.builder().id(senderId).build())
                .textContent(dto.getTextContent())
                .build();

        if (dto.getReplyTo() != null) {
            Message reply = messageRepo.findById(dto.getReplyTo())
                    .orElseThrow(() ->
                            new NotFoundException("replied message not found!")
                    );

            message.setReplyTo(reply);
        }

        messageRepo.save(message);

        saveAttachments(message, dto);

        entityManager.flush();

        MessageResponseDTO response = messageJdbcQuery.findById(message.getId())
                .orElseThrow(() ->
                        new NotFoundException("message was not found")
                );

        signMediaUrls(response);

        return response;
    }

    private void saveAttachments(
            Message message,
            CreateMessageDTO dto
    ) {
        if (dto.getMedia() == null) {
            return;
        }

        List<FileModel> savedMedia = storage.savePrivateFile(
                dto.getMedia(),
                MEDIA_POLICY,
                "chat",
                "media"
        );

        List<MessageAttachment> attachments = IntStream
                .range(0, savedMedia.size())
                .mapToObj(index -> {
                    FileModel file = savedMedia.get(index);

                    return MessageAttachment.builder()
                            .mediaUrl(file.getFilePath())
                            .mediaOrder(index)
                            .mediaType(file.getFileType())
                            .message(message)
                            .build();
                })
                .toList();

        attachmentRepo.saveAll(attachments);
    }

    private void validateMessageContent(CreateMessageDTO dto) {
        if (dto.getMedia() == null && dto.getTextContent() == null) {
            throw new RequestValidationException(
                    "the media and the text cannot be null at the same time"
            );
        }
    }

    private void signMediaUrls(MessageResponseDTO response) {
        response.getMedia().forEach(media ->
                media.setMediaUrl(
                        signer.sign(
                                privateBucket,
                                media.getMediaUrl(),
                                MEDIA_URL_EXPIRATION
                        )
                )
        );
    }

    private void signMediaUrls(List<MessageResponseDTO> messages) {
        messages.forEach(this::signMediaUrls);
    }
}
