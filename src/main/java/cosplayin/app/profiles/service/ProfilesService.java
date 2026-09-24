package cosplayin.app.profiles.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
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

        private final String[] avatarFolder = { "user", "profiles", "avatar" };
        private final String[] bannerFolder = { "user", "profiles", "banner" };

        public Profiles create(String fullname, String avatarUrl, Users user) {
                Profiles profile = Profiles.builder()
                                .user(user)
                                .displayName(fullname)
                                .avatarUrl(avatarUrl)
                                .build();

                return profileRepository.save(profile);
        }

        public DetailProfileDTO getProfileDetails(UUID id) {
                DetailProfileDTO profileDetail = profileRepository.findProfileDetailsById(id)
                                .orElseThrow(() -> new NotFoundException("profile and users was not found"));

                return profileDetail;
        }

        public DetailProfileDTO getProfileDetails(String username) {
                DetailProfileDTO profileDetail = profileRepository.findProfileDetailsByUsername(username)
                                .orElseThrow(() -> new NotFoundException("profile and users was not found"));

                return profileDetail;
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

                profile.setAvatarUrl(avatar.getFilePath());
                profile.setBannerUrl(banner.getFilePath());
                userService.updateUsername(req.getUsername(), curr);
                user.setStatus(UserStatus.ACTIVE);

                DetailProfileDTO projection = profileRepository.findProfileDetailsById(curr)
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

                        if (!oldPathFile.startsWith("https://")) {
                                storageUtils.deletePublicFile(oldPathFile);
                        }

                        profiles.setAvatarUrl(saved.getFilePath());
                }

                if (updateDto.getBanner() != null) {
                        FileModel saved = storageUtils.savePublicFile(updateDto.getBanner(), filePolicy, bannerFolder);

                        String oldPathFile = profiles.getBannerUrl();

                        if (!oldPathFile.startsWith("https://")) {
                                storageUtils.deletePublicFile(oldPathFile);
                        }
                        profiles.setBannerUrl(saved.getFilePath());
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
                List<DetailProfileDTO> rand = profileRepository.findDiscoverProfiles(curr, PageRequest.of(0, 10));

                return rand;
        }

        public List<DetailProfileDTO> searchByUsername(String query) {
                List<DetailProfileDTO> results = profileRepository.searchProfiles(query);

                return results;
        }
}
