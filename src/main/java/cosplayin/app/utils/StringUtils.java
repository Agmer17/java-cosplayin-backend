package cosplayin.app.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class StringUtils {
    private static final SecureRandom sec = new SecureRandom();

    public static String generateRandomToken(int len) {
        byte[] bytes = new byte[len];

        sec.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateConvKey(UUID first, UUID second) {
        UUID smaller = first.compareTo(second) < 0 ? first : second;
        UUID larger = first.compareTo(second) < 0 ? second : first;

        return smaller + ":" + larger;
    }

}
