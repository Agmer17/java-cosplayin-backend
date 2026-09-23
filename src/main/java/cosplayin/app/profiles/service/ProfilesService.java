package cosplayin.app.profiles.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.event.UsersCredentialUpdateEvent;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.profiles.model.dto.SubmitOnBoardingRequests;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.projection.DetailProfileProjection;
import cosplayin.app.profiles.repository.ProfilesRepository;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.service.UsersService;
import cosplayin.app.utils.storage.StorageUtils;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfilesService {
        private final ProfilesRepository profileRepository;
        private final UsersService userService;
        private final StorageUtils storageUtils;
        private final ApplicationEventPublisher eventPublisher;

        public Profiles create(String fullname, String avatarUrl, Users user) {
                Profiles profile = Profiles.builder()
                                .user(user)
                                .displayName(fullname)
                                .avatarUrl(avatarUrl)
                                .build();

                return profileRepository.save(profile);
        }

        public DetailProfileProjection getProfileDetails(UUID id) {
                DetailProfileProjection profileDetail = profileRepository.findProfileDetailsById(id)
                                .orElseThrow(() -> new NotFoundException("profile and users was not found"));

                return profileDetail;
        }

        @Transactional
        public DetailProfileProjection submitOnBoarding(UUID curr, SubmitOnBoardingRequests req) {

                if (req.getBannerPicture() == null || req.getBannerPicture().isEmpty()
                                || req.getProfilePicture() == null
                                || req.getProfilePicture().isEmpty()) {
                        throw new RequestValidationException(
                                        "please provide banner picture and profile picture to complete on boarding!");
                }

                Profiles profile = profileRepository.findById(curr)
                                .orElseThrow(() -> new NotFoundException("your account was not found!"));

                Users user = profile.getUser();

                FileValidationPolicy policy = new FileValidationPolicy(Set.of(SupportedFileType.IMAGE), 5242880);

                FileModel avatar = storageUtils.savePublicFile(req.getProfilePicture(), policy, "user", "profiles",
                                "avatar");
                FileModel banner = storageUtils.savePublicFile(req.getBannerPicture(), policy, "user", "profiles",
                                "banner");

                profile.setAvatarUrl(avatar.getFilePath());
                profile.setBannerUrl(banner.getFilePath());
                userService.updateUsername(req.getUsername(), curr);
                user.setStatus(UserStatus.ACTIVE);

                DetailProfileProjection projection = profileRepository.findProfileDetailsById(curr)
                                .orElseThrow(() -> new NotFoundException("your account was not found!"));

                UsersCredentialUpdateEvent event = new UsersCredentialUpdateEvent(UserCredentials.builder()
                                .id(curr)
                                .status(user.getStatus())
                                .role(user.getRole())
                                .build());

                eventPublisher.publishEvent(event);
                return projection;

        }
}
