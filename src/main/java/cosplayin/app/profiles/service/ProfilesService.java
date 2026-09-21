package cosplayin.app.profiles.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.projection.DetailProfileProjection;
import cosplayin.app.profiles.repository.ProfilesRepository;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfilesService {
    private final ProfilesRepository profileRepository;

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
}
