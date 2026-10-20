package cosplayin.app.config;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.scribejava.apis.DiscordApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class UtilsConfig {

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${DISCORD_CLIENT_ID}")
    private String discordClientId;

    @Value("${DISCORD_CLIENT_SECRET}")
    private String discordClientSecret;

    @Bean("googleOauth")
    OAuth20Service googleOauthService() {
        String googleCallbackUrl = "https://margin-trance-musty.ngrok-free.dev/api/auth/google";
        return new ServiceBuilder(googleClientId)
                .apiSecret(googleClientSecret)
                .defaultScope("email profile")
                .callback(googleCallbackUrl)
                .build(GoogleApi20.instance());
    }

    @Bean("discordOauth")
    OAuth20Service discordOauthSevice() {
        String discordOauthCallback = "https://margin-trance-musty.ngrok-free.dev/api/auth/discord";
        return new ServiceBuilder(discordClientId)
                .apiSecret(discordClientSecret)
                .defaultScope("identify email openid")
                .callback(discordOauthCallback)
                .build(DiscordApi.instance());
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    Tika tika() {
        return new Tika();
    }
}
