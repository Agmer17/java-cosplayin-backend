package cosplayin.app.message.service;

import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.conversation.service.ConversationService;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
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
    public MessageResponseDTO createPrivateMessage(UUID target, UUID curr, CreateMessageDTO dto) {

        if (dto.getMedia() == null && dto.getTextContent() == null) {
            throw new RequestValidationException("the media and the text cannot be null at the same time");
        }

        Conversation conv = conversationService.getOrCreateConversation(
                StringUtils.generateConvKey(curr, target),
                ConversationType.PRIVATE,
                curr,
                target
        );


        Users currUsers = Users.builder().id(curr).build();
        Message message = Message.builder().conversation(conv).sender(currUsers)
                .textContent(dto.getTextContent())
                .build();

        if (dto.getReplyTo() != null) {
            Message reply = messageRepo.findById(dto.getReplyTo()).orElseThrow(() -> new NotFoundException("replied message not found!"));
            message.setReplyTo(reply);
        }

        messageRepo.save(message);
        if (dto.getMedia() != null) {

            FileValidationPolicy mediaPolicy = new FileValidationPolicy(
                    Set.of(SupportedFileType.IMAGE, SupportedFileType.VIDEO, SupportedFileType.DOCUMENT),
                    MAX_FILE_SIZE
            );

            List<FileModel> savedMedia = storage.savePrivateFile(dto.getMedia(), mediaPolicy, "chat", "media");


            List<MessageAttachment> attachments = IntStream.range(0, savedMedia.size())
                    .mapToObj(idx -> MessageAttachment.builder()
                            .mediaUrl(savedMedia.get(idx).getFilePath())
                            .mediaOrder(idx)
                            .mediaType(savedMedia.get(idx).getFileType())
                            .message(message)
                            .build()).toList();

            attachmentRepo.saveAll(attachments);
        }

        entityManager.flush();

        MessageResponseDTO savedMessage = messageJdbcQuery.findById(message.getId()).orElseThrow(() -> new NotFoundException("message was not found"));
        savedMessage.getMedia().forEach(md -> md.setMediaUrl(
                signer.sign(privateBucket,
                        md.getMediaUrl(),
                        Duration.ofHours(1)
                )));

        return savedMessage;
    }


    public List<MessageResponseDTO> getPrivateMessage(UUID curr, UUID target) {
        String key = StringUtils.generateConvKey(curr, target);

        Optional<Conversation> conv = conversationService.getByConversationKey(key);

        if (conv.isEmpty()) {
            return new ArrayList<>();
        }

        List<MessageResponseDTO> dto = messageJdbcQuery.findByConversation(conv.get().getId());


        dto.forEach(rsp -> {
            if (rsp.getMedia() != null) {
                rsp.getMedia().forEach(md -> md.setMediaUrl(signer.sign(privateBucket, md.getMediaUrl(), Duration.ofHours(1))));
            }
        });

        return dto;

    }
}
