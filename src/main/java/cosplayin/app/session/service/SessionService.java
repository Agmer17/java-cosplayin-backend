package cosplayin.app.session.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.redis.connection.SetCondition;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

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
            return null;
        }

        return new SessionAuthContext(model, cred);
    }

    public void revokeSession(String token) {
        redisTemplate.delete(generateSessionKey(token));
    }

    public String generateSessionKey(String accessToken) {
        StringBuilder sb = new StringBuilder("session:");

        sb.append(accessToken);

        return sb.toString();

    }

    public String generateCredentialsLookupKey(UUID id) {
        StringBuilder sb = new StringBuilder("users:cred:");
        sb.append(id.toString());
        return sb.toString();
    }

}
