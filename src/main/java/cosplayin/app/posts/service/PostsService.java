package cosplayin.app.posts.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.posts.model.dto.CreatePostsDTO;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.model.entity.PostsMedia;
import cosplayin.app.posts.model.type.PostsStatus;
import cosplayin.app.posts.repository.PostsMediaRepository;
import cosplayin.app.posts.repository.PostsRepository;
import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.user.model.entity.Users;
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

    private final ProfilesService profilesService;

    private final StorageUtils storageUtils;

    @Transactional
    public PostsResponse createPosts(CreatePostsDTO dto, UUID curr) {

        if (dto.getMedia() == null || dto.getMedia().isEmpty()) {
            throw new RequestValidationException("you need to provide at least 1 image/video in order to make a posts");
        }

        FileValidationPolicy policy = new FileValidationPolicy(Set.of(SupportedFileType.IMAGE, SupportedFileType.VIDEO),
                10485760);

        List<FileModel> saved = storageUtils.savePrivateFile(dto.getMedia(), policy, "posts", "media");

        Profiles authorProfile = profilesService.getProfile(curr);
        Users authorData = authorProfile.getUser();

        PostsStatus postsStatus = (authorProfile.getVisibility() == ProfilesVisibility.PRIVATE)
                ? PostsStatus.HIDDEN
                : PostsStatus.VISIBLE;

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
                            .build();

                    return md;
                }).toList();

        postsRepository.save(posts);
        mediaRepository.saveAll(media);

        PostsResponse response = postsRepository.findPostDetailsById(posts.getId())
                .orElseThrow(() -> new NotFoundException("posts not found"));

        return response;

    }
}
