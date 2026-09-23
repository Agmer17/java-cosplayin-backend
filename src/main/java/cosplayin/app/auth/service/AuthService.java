package cosplayin.app.auth.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import cosplayin.app.auth.model.dto.DiscordCallbackDto;
import cosplayin.app.auth.model.dto.GoogleCallbackDto;
import cosplayin.app.auth.model.entity.UserAuth;
import cosplayin.app.auth.model.projection.AuthCredentialsProjections;
import cosplayin.app.auth.model.type.AuthProvider;
import cosplayin.app.auth.repository.AuthRepository;
import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.exception.model.FatalErrorExceptions;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.session.service.SessionService;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.service.UsersService;
import cosplayin.app.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Qualifier("googleOauth")
    private final OAuth20Service googleOauth;

    @Qualifier("discordOauth")
    private final OAuth20Service discordOauth;

    private final ObjectMapper mapper;

    private final AuthRepository authRepository;
    private final UsersService usersService;
    private final ProfilesService profilesService;

    private final SessionService sessionService;

    public String getGoogleLoginUrl() {
        return this.googleOauth.getAuthorizationUrl();

    }

    public String handleGoogleCallback(String token) {
        OAuth2AccessToken accessToken;
        try {
            accessToken = this.googleOauth.getAccessToken(token);
        } catch (Exception e) {
            throw new FatalErrorExceptions("Cannot authorize the google login, try again another time");
        }

        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo");

        try {
            this.googleOauth.signRequest(accessToken, request);
        } catch (Exception e) {
            throw new FatalErrorExceptions(
                    "something wrong while trying to login with google please try again another time");
        }

        try (Response response = this.googleOauth.execute(request)) {
            String jsonData = response.getBody();
            GoogleCallbackDto callbackData = mapper.readValue(jsonData, GoogleCallbackDto.class);

            UserCredentials cred = this.getOrRegisterUser(callbackData);

            String authToken = sessionService.issuedASession(cred);

            return authToken;
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new FatalErrorExceptions(
                    "something wrong while trying to login with google, please try again another time : "
                            + e.getMessage());
        }
    }

    @Transactional
    public UserCredentials getOrRegisterUser(GoogleCallbackDto dto) {
        AuthCredentialsProjections projections = this.authRepository.findCredentialsByProviderAndProviderOpenId(
                AuthProvider.GOOGLE, dto.getSub()).orElse(null);

        if (projections != null) {
            return UserCredentials.builder()
                    .id(projections.getUserId())
                    .role(projections.getUserRoles())
                    .status(projections.getStatus())
                    .build();
        }

        String tempUsername = dto.getName();
        tempUsername = tempUsername.toLowerCase().replace(" ", "_") + "_" + StringUtils.generateRandomToken(12);
        Users user = this.usersService.createUser(tempUsername, UserRoles.ADMIN);

        this.authRepository.save(
                UserAuth.builder()
                        .user(user)
                        .provider(AuthProvider.GOOGLE)
                        .providerOpenId(dto.getSub())
                        .email(dto.getEmail())
                        .build());

        this.profilesService.create(dto.getName(), dto.getPicture(), user);

        return UserCredentials.builder()
                .id(user.getId())
                .role(user.getRole())
                .status(user.getStatus())
                .build();

    }

    public String getDiscordLoginUrl() {
        return this.discordOauth.getAuthorizationUrl();
    }

    public String handleDiscordCallback(String token) {
        OAuth2AccessToken accessToken;
        try {
            accessToken = this.discordOauth.getAccessToken(token);
        } catch (Exception e) {
            throw new FatalErrorExceptions("Cannot authorize the discord login, try again another time");
        }

        OAuthRequest discordRequest = new OAuthRequest(Verb.GET, "https://discord.com/api/users/@me");

        try {
            this.discordOauth.signRequest(accessToken, discordRequest);
        } catch (Exception e) {
            throw new FatalErrorExceptions(
                    "something wrong while trying to login with discord please try again another time "
                            + e.getMessage());
        }

        try (Response response = this.discordOauth.execute(discordRequest)) {
            String jsonBody = response.getBody();
            DiscordCallbackDto dto = mapper.readValue(jsonBody, DiscordCallbackDto.class);

            UserCredentials cred = this.getOrRegisterUser(dto);

            String authToken = this.sessionService.issuedASession(cred);

            return authToken;

        } catch (Exception e) {
            throw new FatalErrorExceptions(
                    "something wrong while trying to login with discrod, please try again another time : "
                            + e.getMessage());
        }

    }

    @Transactional
    public UserCredentials getOrRegisterUser(DiscordCallbackDto dto) {
        AuthCredentialsProjections projections = this.authRepository.findCredentialsByProviderAndProviderOpenId(
                AuthProvider.DISCORD, dto.getId()).orElse(null);

        if (projections != null) {
            return UserCredentials.builder()
                    .id(projections.getUserId())
                    .role(projections.getUserRoles())
                    .status(projections.getStatus())
                    .build();
        }

        Users user = this.usersService.createUser(dto.getUsername(), UserRoles.ADMIN, UserStatus.ACTIVE);

        this.authRepository.save(
                UserAuth.builder()
                        .user(user)
                        .provider(AuthProvider.DISCORD)
                        .providerOpenId(dto.getId())
                        .email(dto.getEmail())
                        .build());

        String avatarImage = "https://cdn.discordapp.com/avatars/" + dto.getId() + "/" + dto.getAvatar() + ".png";
        this.profilesService.create(dto.getGlobalName(), avatarImage, user);

        return UserCredentials.builder()
                .id(user.getId())
                .role(user.getRole())
                .status(user.getStatus())
                .build();

    }

    public void logoutUser(String token) {
        sessionService.revokeSession(token);
    }

}
