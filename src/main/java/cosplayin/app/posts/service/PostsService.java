package cosplayin.app.posts.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.posts.model.dto.CreatePostsDTO;
import cosplayin.app.posts.model.dto.PostsMediaResponse;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.model.entity.PostsMedia;
import cosplayin.app.posts.model.type.PostsStatus;
import cosplayin.app.posts.repository.PostsJdbcDao;
import cosplayin.app.posts.repository.PostsMediaRepository;
import cosplayin.app.posts.repository.PostsRepository;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.utils.SignerUrlUtils;
import cosplayin.app.utils.storage.StorageUtils;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostsService {
        private final PostsRepository postsRepository;
        private final PostsMediaRepository mediaRepository;
        private final PostsJdbcDao postsQueryRepo;

        private final ProfilesService profilesService;

        private final StorageUtils storageUtils;

        private final SignerUrlUtils signer;

        @Transactional
        public PostsResponse createPosts(CreatePostsDTO dto, UUID curr) {

                if (dto.getMedia() == null || dto.getMedia().isEmpty()) {
                        throw new RequestValidationException(
                                        "you need to provide at least 1 image/video in order to make a posts");
                }

                FileValidationPolicy policy = new FileValidationPolicy(
                                Set.of(SupportedFileType.IMAGE, SupportedFileType.VIDEO),
                                10485760);

                List<FileModel> saved = storageUtils.savePrivateFile(dto.getMedia(), policy, "posts", "media");

                Profiles authorProfile = profilesService.getProfile(curr);
                Users authorData = authorProfile.getUser();

                PostsStatus postsStatus = PostsStatus.VISIBLE;

                Posts posts = Posts.builder()
                                .author(authorData)
                                .caption(dto.getCaption())
                                .commentAvailability(dto.getCommentStatus())
                                .status(postsStatus)
                                .build();

                List<PostsMedia> media = IntStream.range(0, saved.size())
                                .mapToObj(idx -> {
                                        PostsMedia md = PostsMedia.builder()
                                                        .posts(posts)
                                                        .displayOrder(Short.valueOf(Integer.valueOf(idx).shortValue()))
                                                        .mediaType(saved.get(idx).getFileType())
                                                        .mediaUrl(signer.generateSignedUrl(saved.get(idx).getFilePath(),
                                                                        Duration.ofMinutes(5)))
                                                        .build();

                                        return md;
                                }).toList();

                postsRepository.save(posts);
                mediaRepository.saveAll(media);

                DetailProfileDTO authorResponseDto = DetailProfileDTO.builder()
                                .id(curr)
                                .displayName(authorProfile.getDisplayName())
                                .bio(authorProfile.getBio())
                                .avatarUrl(authorProfile.getAvatarUrl())
                                .bannerUrl(authorProfile.getBannerUrl())
                                .visibility(authorProfile.getVisibility())
                                .username(authorData.getUsername())
                                .userStatus(authorData.getStatus())
                                .userRole(authorData.getRole())
                                .build();

                List<PostsMediaResponse> mediaResponses = media.stream().map(m -> {
                        PostsMediaResponse resp = PostsMediaResponse.builder()
                                        .mediaId(m.getId())
                                        .mediaUrl(m.getMediaUrl())
                                        .mediaType(m.getMediaType())
                                        .displayOrder(m.getDisplayOrder())
                                        .createdAt(m.getCreatedAt())
                                        .build();
                        return resp;
                }).toList();

                PostsResponse response = PostsResponse.builder()
                                .postsId(posts.getId())
                                .author(authorResponseDto)
                                .caption(posts.getCaption())
                                .status(postsStatus)
                                .likeCount(0L)
                                .bookmarkCount(0L)
                                .shareCount(0L)
                                .createdAt(posts.getCreatedAt())
                                .media(mediaResponses)
                                .build();

                return response;
        }

        public PostsResponse getPostsDetail(UUID postId, UUID curr) {
                PostsResponse response = postsQueryRepo.getPostsDetailById(postId, curr)
                                .orElseThrow(() -> new NotFoundException("posts with this id not found"));

                if (response.getStatus().equals(PostsStatus.HIDDEN)) {
                        throw new NotFoundException("posts not found");
                }

                if (response.getAuthor().visibility().equals(ProfilesVisibility.PRIVATE)) {
                        // nanti lakuin pemeriksaan kalo fitur follow udah ada
                }

                // List<PostsMediaResponse> media = mediaRepository.findMediaByPostId(postId);

                // response.setMedia(media);

                signMediaUrl(response.getMedia());

                return response;
        }

        public List<PostsResponse> getAllPosts(int page, UUID id) {
                List<PostsResponse> allPosts = postsQueryRepo.findAllPostsWithDetails(20, page, id);

                allPosts.forEach(p -> {
                        signMediaUrl(p.getMedia());
                });
                return allPosts;
        }

        public List<PostsResponse> getPostsFeed(int page, UUID id) {
                List<PostsResponse> feedData = postsQueryRepo.findRandomPosts(20, page, id);
                feedData.forEach(p -> {
                        signMediaUrl(p.getMedia());
                });

                return feedData;
        }

        public void deletePosts(UUID postsId, UserCredentials cred) {

                Posts posts = postsRepository.findById(postsId)
                                .orElseThrow(() -> new NotFoundException("posts not found!"));

                Users author = posts.getAuthor();

                boolean isOwner = author.getId().equals(cred.getId());
                boolean isAdminOrMod = cred.getRole().equals(UserRoles.ADMIN)
                                || cred.getRole().equals(UserRoles.MODERATOR);

                if (!isOwner && !isAdminOrMod) {
                        throw new ForbiddenAccessExceptions("you don't have permission to delete this posts");
                }

                List<PostsMedia> mediaToDelete = mediaRepository.findByPosts(posts);

                List<String> deletedMediaPath = mediaToDelete.stream().map(m -> m.getMediaUrl()).toList();

                storageUtils.deletePrivateFile(deletedMediaPath);

                postsRepository.delete(posts);
                mediaRepository.deleteAll(mediaToDelete);

        }

        public List<PostsResponse> getPostsFromUsers(String username, int page, UUID curr) {
                Profiles profile = profilesService.getProfile(username);

                if (profile.getVisibility().equals(ProfilesVisibility.PRIVATE)) {
                        // do follow logic buatv cek udah saling follow belom
                }

                List<PostsResponse> fromUsers = postsQueryRepo.findAllByUsername(username, 20, page, curr);

                fromUsers.forEach(p -> {
                        signMediaUrl(p.getMedia());
                });
                return fromUsers;
        }

        public List<PostsResponse> searchPostsByKeyword(String keyword, UUID curr, int page) {
                List<PostsResponse> responses = postsQueryRepo.searchPosts(keyword, 20, page, curr);

                responses.forEach(p -> {
                        signMediaUrl(p.getMedia());
                });

                return responses;
        }

        public List<PostsResponse> findPostsDetailsInIds(List<UUID> ids, UUID curr) {

                List<PostsResponse> response = postsQueryRepo.findPostsInId(ids, curr);
                response.forEach(p -> {
                        signMediaUrl(p.getMedia());
                });

                return response;
        }

        public Posts findEntityById(UUID id) {
                return postsRepository.findById(id).orElseThrow(() -> new NotFoundException("posts not found"));
        }

        private void signMediaUrl(List<PostsMediaResponse> data) {
                data.forEach(med -> {
                        String url = "private/" + med.getMediaUrl();
                        med.setMediaUrl(signer.generateSignedUrl(url, Duration.ofMinutes(5)));
                });
        }
}
