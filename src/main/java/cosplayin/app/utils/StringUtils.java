package cosplayin.app.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class StringUtils {
    private static final SecureRandom sec = new SecureRandom();

    public static String generateRandomToken(int len) {
        byte[] bytes = new byte[len];

        sec.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
