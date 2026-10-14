package cosplayin.app.followers.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.followers.model.dto.FollowerResponse;
import cosplayin.app.followers.model.entity.FollowRequests;
import cosplayin.app.followers.model.entity.Followers;
import cosplayin.app.followers.model.type.FollowStatus;
import cosplayin.app.followers.repository.FollowRequestsRepository;
import cosplayin.app.followers.repository.FollowerJdbcQuery;
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
    private final ProfilesService profilesService;

    private final FollowerJdbcQuery followerQuery;

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

        if (targetProfile.visibility().equals(ProfilesVisibility.PRIVATE)) {

            try {
                followRequestsRepo.saveAndFlush(FollowRequests.builder()
                        .target(targetUsers)
                        .build());

            } catch (DataIntegrityViolationException e) {
                throw new ResourceConflictExceptions("you've already requested to follow this person!");
            }

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
            try {
                followerRepo.saveAndFlush(follow);
                profilesService.increamnetFollower(target);

            } catch (DataIntegrityViolationException e) {
                throw new ResourceConflictExceptions("you've already follow this person");
            }

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
    }

    @Transactional
    public void deleteFollow(UUID curr, UUID target) {
        Followers follow = followerRepo.findByFollower_IdAndFollowing_Id(curr, target)
                .orElseThrow(() -> new NotFoundException("you're not following this person!"));
        followerRepo.delete(follow);
        profilesService.decreamentFollower(target);
    }

    @Transactional
    public void deleteFollowRequests(UUID curr, UUID target) {
        FollowRequests req = followRequestsRepo.findByRequester_IdAndTarget_Id(curr, target)
                .orElseThrow(() -> new NotFoundException("you doens't follow this person"));
        followRequestsRepo.delete(req);
    }

    public List<FollowerResponse> getFollowerFromUsers(UUID target, UUID curr) {
        return followerQuery.findFollowerFrom(target, curr);
    }
}
