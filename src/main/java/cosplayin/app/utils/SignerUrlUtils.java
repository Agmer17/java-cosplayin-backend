package cosplayin.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignerUrlUtils {

    @Value("${MEDIA_ACCESS_SECRET}")
    private String secretKey;

    // filepath should be on of public/private
    public String generateSignedUrl(String filePath, Duration ttl) {
        long expires = Instant.now().plus(ttl).getEpochSecond();
        String signature = sign(filePath, expires);
        String signedUrl = String.format("/%s?expires=%d&sig=%s", filePath, expires, signature);

        System.out.println(signedUrl);
        return signedUrl;
    }

    public boolean isValid(String filePath, long expires, String signature) {
        if (Instant.now().getEpochSecond() > expires) {
            return false;
        }
        String expectedSig = sign(filePath, expires);
        return MessageDigest.isEqual(
                expectedSig.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String filePath, long expires) {
        try {
            String payload = filePath + ":" + expires;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign URL", e);
        }
    }
}
