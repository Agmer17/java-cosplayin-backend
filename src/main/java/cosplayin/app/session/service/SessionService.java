package cosplayin.app.session.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.SetCondition;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cosplayin.app.core.event.UsersCredentialUpdateEvent;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.session.model.SessionAuthContext;
import cosplayin.app.session.model.SessionDataModel;
import cosplayin.app.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    public String issuedASession(UserCredentials cred) {
        String token = StringUtils.generateRandomToken(36);

        SessionDataModel model = SessionDataModel.builder()
                .id(cred.getId())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .issuedAt(LocalDateTime.now())
                .build();

        Expiration ttl = Expiration.from(Duration.ofDays(7));
        RedisSerializer<Object> valSer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) conn -> {
            conn.stringCommands().set(generateSessionKey(token).getBytes(), valSer.serialize(model),
                    SetCondition.upsert(), ttl);

            conn.stringCommands().set(
                    generateCredentialsLookupKey(cred.getId()).getBytes(),
                    valSer.serialize(cred),
                    SetCondition.upsert(),
                    ttl);

            conn.stringCommands().set(generateSessionKey(token, cred.getId()).getBytes(), valSer.serialize(model),
                    SetCondition.upsert(), ttl);
            return null;
        });

        return token;
    }

    public SessionAuthContext findSessionAndCreds(String accessToken) {
        SessionDataModel model = (SessionDataModel) redisTemplate.opsForValue()
                .get(generateSessionKey(accessToken));

        if (model == null) {
            return null;
        }

        UserCredentials cred = (UserCredentials) redisTemplate.opsForValue()
                .get(generateCredentialsLookupKey(model.getId()));

        if (cred == null) {
            new SessionAuthContext(model, null);
        }

        return new SessionAuthContext(model, cred);
    }

    public void revokeSession(String token) {
        Set<String> delKeys = new HashSet<>();
        ScanOptions opt = ScanOptions.scanOptions()
                .match(generateSessionKey(token) + "*")
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(opt)) {

            cursor.forEachRemaining(delKeys::add);
        }

        redisTemplate.unlink(delKeys);
    }

    @Async
    @EventListener
    @Retryable(includes = RedisConnectionFailureException.class, maxRetries = 2, delay = 1000, multiplier = 2)
    public void revokeAllSessionByUser(UUID id) {
        Set<String> delKeys = new HashSet<>();
        ScanOptions opt = ScanOptions.scanOptions()
                .match(generateSessionKey("*", id))
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(opt)) {

            cursor.forEachRemaining(delKeys::add);
        }

        String credsData = generateCredentialsLookupKey(id);
        delKeys.add(credsData);
        redisTemplate.unlink(delKeys);

    }

    public String generateSessionKey(String accessToken) {
        StringBuilder sb = new StringBuilder("session:");

        sb.append(accessToken);

        return sb.toString();

    }

    public String generateSessionKey(String accessToken, UUID id) {
        StringBuilder sb = new StringBuilder("session:");

        sb.append(accessToken);
        sb.append(":");
        sb.append(id.toString());

        return sb.toString();

    }

    @SuppressWarnings("unchecked")
    @Async
    @EventListener
    @Retryable(includes = RedisConnectionFailureException.class, maxRetries = 2, delay = 1000, multiplier = 2)
    public void updateSessionCredentialsAync(UsersCredentialUpdateEvent update) {
        RedisSerializer<Object> valSer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.stringCommands().set(
                    generateCredentialsLookupKey(update.newCredentials().getId()).getBytes(),
                    valSer.serialize(update.newCredentials()),
                    SetCondition.upsert(),
                    Expiration.keepTtl());
            return true;
        });
    }

    public String generateCredentialsLookupKey(UUID id) {
        StringBuilder sb = new StringBuilder("users:cred:");
        sb.append(id.toString());
        return sb.toString();
    }

}
