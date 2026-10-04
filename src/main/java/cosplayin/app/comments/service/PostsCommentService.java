package cosplayin.app.comments.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cosplayin.app.comments.model.dto.CommentResponse;
import cosplayin.app.comments.model.dto.CreateCommentDto;
import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.comments.model.projection.DeleteCommentInfoProjection;
import cosplayin.app.comments.repository.CommentJdbcQuery;
import cosplayin.app.comments.repository.PostsCommentRepository;
import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.model.type.PostsCommentStatus;
import cosplayin.app.posts.service.PostsService;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.user.model.entity.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostsCommentService {

    private final PostsService postsService;
    private final PostsCommentRepository commentRepository;
    private final CommentJdbcQuery commentQuery;

    @Transactional
    public CommentResponse createComment(CreateCommentDto dto, UUID currentUser, UUID postId) {
        Posts posts = postsService.findEntityById(postId);

        if (posts.getCommentAvailability().equals(PostsCommentStatus.NOT_AVAIBLE)) {
            throw new ForbiddenAccessExceptions("you can't comment on this posts, the author disable it");
        }

        PostsComment parentComment = null;

        if (dto.getReplyTo() != null) {
            parentComment = commentRepository.findById(dto.getReplyTo())
                    .orElseThrow(() -> new NotFoundException("comment parent not found"));
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
        }

        PostsComment comment = PostsComment.builder()
                .user(Users.builder().id(currentUser).build())
                .posts(posts)
                .comment(dto.getComment())
                .parentComment(parentComment)
                .build();

        commentRepository.saveAndFlush(comment);
        posts.setCommentCount(posts.getCommentCount() + 1);

        CommentResponse saved = commentQuery.getByCommentId(comment.getId())
                .orElseThrow(() -> new NotFoundException("comment was not found!"));

        return saved;
    }

    public List<CommentResponse> getCommentResponseFromPosts(UUID postsId) {

        PostsResponse resp = postsService.getPostsDetail(postsId, null);

        if (resp.getAuthor().visibility().equals(ProfilesVisibility.PRIVATE)) {
            // do something
        }

        return commentQuery.getCommentFromPosts(postsId);
    }

    public List<CommentResponse> getReplyFromComment(UUID commentId) {

        List<CommentResponse> response = commentQuery.getReplyFrom(commentId);

        System.out.println("JUMLAH HASIL : " + response.size());

        return response;
    }

    public void deleteComment(UUID commentId, UserCredentials cred) {

        DeleteCommentInfoProjection comment = commentRepository
                .findDeleteProjectionById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        UUID commentAuthorId = comment.getCommentAuthorId();
        UUID postAuthorId = comment.getPostAuthorId();

        boolean canDelete = cred.getId().equals(commentAuthorId)
                || postAuthorId.equals(postAuthorId)
                || cred.getRole().equals(UserRoles.ADMIN)
                || cred.getRole().equals(UserRoles.MODERATOR);

        if (!canDelete) {
            throw new ForbiddenAccessExceptions("you don't have permission to delete this comment!");
        }

        commentRepository.deleteById(commentId);

    }
}
