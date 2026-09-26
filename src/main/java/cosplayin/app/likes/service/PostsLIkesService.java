package cosplayin.app.likes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.likes.model.dto.DetailLikesResponseDto;
import cosplayin.app.likes.model.entity.PostsLikes;
import cosplayin.app.likes.repository.PostsLikesRepository;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.service.PostsService;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostsLIkesService {
    private final PostsLikesRepository likesRepository;

    private final PostsService postsService;
    private final ProfilesService profilesService;

    @Transactional
    public LocalDateTime createLike(UUID postsId, UUID userId) {
        Posts postsData = postsService.findEntityById(postsId);

        System.out.println("DATA POSTS : " + postsData);

        Users user = Users.builder().id(userId).build();

        PostsLikes likes = PostsLikes.builder()
                .posts(postsData)
                .user(user)
                .build();

        try {
            likesRepository.saveAndFlush(likes);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictExceptions("you already liked this posts");
        }

        postsData.setLikeCount(postsData.getLikeCount() + 1);
        return LocalDateTime.now();
    }

    public List<DetailLikesResponseDto> getLikesDetailFromPosts(UUID id) {
        return likesRepository.findAllDetailsByPostsId(id);
    }

    public List<PostsResponse> getLikedPostsFromUsers(String username, UUID curr) {
        List<UUID> likedPostsIds = likesRepository.findLikedPostsIdsByUsername(username);

        Profiles profile = profilesService.getProfile(username);

        if (profile.getVisibility().equals(ProfilesVisibility.PRIVATE)) {
            // lakuin validasi nanti pas follow udah jadi
        }

        List<PostsResponse> data = postsService.findPostsDetailsInIds(likedPostsIds, curr);

        return data;
    }

    public List<PostsResponse> getLikedPostsFromUsers(UUID curr) {
        List<UUID> likedPostsIds = likesRepository.findLikedPostsIdsByUserId(curr);

        List<PostsResponse> data = postsService.findPostsDetailsInIds(likedPostsIds, curr);

        return data;
    }

    public void deleteLike(UUID curr, UUID likesId) {
        PostsLikes likes = likesRepository.findById(likesId)
                .orElseThrow(() -> new NotFoundException("likes data was not found"));

        if (!likes.getUser().getId().equals(curr)) {
            throw new ForbiddenAccessExceptions("you can't delete this likes");
        }
        likesRepository.delete(likes);
    }
}
