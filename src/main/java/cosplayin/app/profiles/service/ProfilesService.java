package cosplayin.app.profiles.service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.event.UsersCredentialUpdateEvent;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.dto.ProfileUpdateDto;
import cosplayin.app.profiles.model.dto.SubmitOnBoardingRequests;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.repository.ProfileJdbcQuery;
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
        private final ProfileJdbcQuery profileJdbcQuery;

        @Value("${storage.s3.endpoint}")
        private String s3StorageEndpoint;

        @Value("${storage.s3.bucket.public}")
        private String publicReadBucket;

        private final String[] avatarFolder = { "user", "profiles", "avatar" };
        private final String[] bannerFolder = { "user", "profiles", "banner" };

        public Profiles create(String fullname, String avatarUrl, Users user) {
                Profiles profile = Profiles.builder()
                                .user(user)
                                .displayName(fullname)
                                .avatarUrl(avatarUrl)
                                .followerCount(0)
                                .build();

                return profileRepository.save(profile);
        }

        public DetailProfileDTO getProfileDetails(UUID id) {

            return profileJdbcQuery.findProfileDetailsById(id)
                            .orElseThrow(() -> new NotFoundException("profile and users was not found"));
        }

        public DetailProfileDTO getProfileDetails(String username, UUID curr) {
            return profileJdbcQuery.findProfileDetailsByUsername(username, curr)
                            .orElseThrow(() -> new NotFoundException("profile and users was not found"));
        }

        @Transactional
        public DetailProfileDTO submitOnBoarding(UUID curr, SubmitOnBoardingRequests req) {
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

                FileModel avatar = storageUtils.savePublicFile(req.getProfilePicture(), policy, avatarFolder);
                FileModel banner = storageUtils.savePublicFile(req.getBannerPicture(), policy, bannerFolder);

                profile.setAvatarUrl(this.storageUtils.signPublicFileUrl(avatar.getFilePath()));
                profile.setBannerUrl(this.storageUtils.signPublicFileUrl(banner.getFilePath()));
                userService.updateUsername(req.getUsername(), curr);
                user.setStatus(UserStatus.ACTIVE);

                DetailProfileDTO projection = profileJdbcQuery.findProfileDetailsById(curr)
                                .orElseThrow(() -> new NotFoundException("your account was not found!"));

                UsersCredentialUpdateEvent event = new UsersCredentialUpdateEvent(UserCredentials.builder()
                                .id(curr)
                                .status(user.getStatus())
                                .role(user.getRole())
                                .build());

                eventPublisher.publishEvent(event);
                return projection;

        }

        @Transactional
        public DetailProfileDTO updateProfiles(UUID curr, ProfileUpdateDto updateDto) {
                Profiles profiles = profileRepository.findById(curr)
                                .orElseThrow(() -> new NotFoundException("users not found"));

                if (updateDto.getDisplayName() != null) {
                        profiles.setDisplayName(updateDto.getDisplayName());
                }

                if (updateDto.getBio() != null) {
                        profiles.setBio(updateDto.getBio());
                }

                if (updateDto.getVisibility() != null) {
                        profiles.setVisibility(updateDto.getVisibility());
                }

                if (updateDto.getGender() != null) {
                        profiles.setGender(updateDto.getGender());
                }

                FileValidationPolicy filePolicy = new FileValidationPolicy(Set.of(SupportedFileType.IMAGE), 5242880);
                if (updateDto.getAvatar() != null) {
                        FileModel saved = storageUtils.savePublicFile(updateDto.getAvatar(), filePolicy, avatarFolder);

                        String oldPathFile = profiles.getAvatarUrl();



                        if (oldPathFile != null && !oldPathFile.contains("lh3.googleusercontent.com") && !oldPathFile.contains("cdn.discordapp.com")) {
                                storageUtils.deletePublicFile(this.extractObjectKey(oldPathFile));
                        }

                        profiles.setAvatarUrl(this.storageUtils.signPublicFileUrl(saved.getFilePath()));
                }

                if (updateDto.getBanner() != null) {
                        FileModel saved = storageUtils.savePublicFile(updateDto.getBanner(), filePolicy, bannerFolder);

                        String oldPathFile = profiles.getBannerUrl();

                        if (oldPathFile != null && !oldPathFile.contains("lh3.googleusercontent.com") && !oldPathFile.contains("cdn")) {
                                storageUtils.deletePublicFile(this.extractObjectKey(oldPathFile));
                        }
                        profiles.setBannerUrl(this.storageUtils.signPublicFileUrl(saved.getFilePath()));
                }

                return DetailProfileDTO.builder()
                                .id(curr)
                                .displayName(profiles.getDisplayName())
                                .bio(profiles.getBio())
                                .avatarUrl(profiles.getAvatarUrl())
                                .bannerUrl(profiles.getBannerUrl())
                                .visibility(profiles.getVisibility())
                                .username(profiles.getUser().getUsername())
                                .userStatus(profiles.getUser().getStatus())
                                .userRole(profiles.getUser().getRole())
                                .build();
        }

        public List<DetailProfileDTO> findRandomProfile(UUID curr) {

            return profileJdbcQuery.findDiscoverProfiles(curr, 0, 10);
        }

        public List<DetailProfileDTO> searchByUsername(String query) {

            return profileJdbcQuery.searchProfiles(query);
        }

        public Profiles getProfile(UUID id) {
                return profileRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("users and profile was not found"));

        }

        public Profiles getProfile(String username) {
                return profileRepository.findByUsername(username)
                                .orElseThrow(() -> new NotFoundException(username + " profile's not found!"));
        }

        public void increamnetFollower(UUID target) {
                profileRepository.incrementProfilesFollowerCount(target);
        }
        public void increamentFollowing(UUID target) {
                profileRepository.incrementProfileFollowingCount(target);
        }

        public void decreamentFollower(UUID target) {
                profileRepository.decreamentProfilesFollowerCount(target);
        }

        public void decreamentFollowing(UUID target) {
                profileRepository.decreamentProfilesFollowingCount(target);
        }

        private String extractObjectKey(String url) {
                String path = URI.create(url).getPath();

                String bucketPrefix = "/development-public-bucket/";

                return path.substring(path.indexOf(bucketPrefix) + bucketPrefix.length());
        }
}


