package cosplayin.app.followers.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.followers.model.dto.FollowerResponse;
import cosplayin.app.followers.model.entity.FollowRequests;
import cosplayin.app.followers.model.entity.Followers;
import cosplayin.app.followers.model.type.FollowStatus;
import cosplayin.app.followers.repository.FollowRequestsRepository;
import cosplayin.app.followers.repository.FollowerRepository;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowerService {

    private final FollowerRepository followerRepo;
    private final FollowRequestsRepository followRequestsRepo;
    // private final UsersService usersService;
    private final ProfilesService profilesService;

    @Transactional
    public FollowerResponse createFollow(UUID curr, UUID target) {

        if (curr.equals(target)) {
            throw new ResourceConflictExceptions("you can't follow yourself!");
        }

        DetailProfileDTO targetProfile = profilesService.getProfileDetails(target);

        Users current = Users.builder()
                .id(curr)
                .build();

        Users targetUsers = Users.builder().id(targetProfile.id()).build();

        Followers follow = Followers.builder()
                .follower(current)
                .following(targetUsers)
                .build();

        try {

            if (targetProfile.visibility().equals(ProfilesVisibility.PRIVATE)) {
                followRequestsRepo.save(FollowRequests.builder()
                        .target(targetUsers)
                        .build());

                return FollowerResponse.builder()
                        .user(SimpleProfileDTO.builder()
                                .id(targetProfile.id())
                                .displayName(targetProfile.displayName())
                                .avatarUrl(targetProfile.avatarUrl())
                                .visibility(targetProfile.visibility())
                                .username(targetProfile.username())
                                .userRole(targetProfile.userRole())
                                .build())
                        .status(FollowStatus.REQUESTED)
                        .build();

            } else {
                followerRepo.saveAndFlush(follow);
                return FollowerResponse.builder()
                        .user(SimpleProfileDTO.builder()
                                .id(targetProfile.id())
                                .displayName(targetProfile.displayName())
                                .avatarUrl(targetProfile.avatarUrl())
                                .visibility(targetProfile.visibility())
                                .username(targetProfile.username())
                                .userRole(targetProfile.userRole())
                                .build())
                        .status(FollowStatus.FOLLOWING)
                        .build();
            }

        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictExceptions("you already follow this person!");
        }

    }

}
