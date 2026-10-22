package cosplayin.app.comments.service;

import java.util.List;
import java.util.UUID;

import cosplayin.app.followers.service.FollowerService;
import cosplayin.app.notification.event.PostsCommentNotificationEvent;
import cosplayin.app.notification.event.PostsCommentRepliesNotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
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
    private final FollowerService followerService;

    private final ApplicationEventPublisher eventPublisher;



    @Transactional
    public CommentResponse createComment(CreateCommentDto dto, UUID currentUser, UUID postId) {

        PostsResponse posts = postsService.getPostsDetail(postId, currentUser);

        boolean isReply = false;

        if (posts.getAuthor().visibility().equals(ProfilesVisibility.PRIVATE)) {
            if (followerService.isNotFollowedBy(currentUser, posts.getAuthor().id())) {
                throw new ForbiddenAccessExceptions("you cant comment on this posts");
            }
        }


        if (posts.getCommentAvailability().equals(PostsCommentStatus.NOT_AVAIBLE)) {
            throw new ForbiddenAccessExceptions("you can't comment on this posts, the author disable it");
        }

        PostsComment parentComment = null;

        if (dto.getReplyTo() != null) {
            parentComment = commentRepository.findById(dto.getReplyTo())
                    .orElseThrow(() -> new NotFoundException("comment parent not found"));
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);

            isReply = true;
        }


        PostsComment comment = PostsComment.builder()
                .user(Users.builder().id(currentUser).build())
                .posts(Posts.builder().id(posts.getPostsId()).build())
                .comment(dto.getComment())
                .parentComment(parentComment)
                .likeCount(0)
                .build();

        commentRepository.saveAndFlush(comment);
        posts.setCommentCount(posts.getCommentCount() + 1);


        if (isReply) {
            eventPublisher.publishEvent(PostsCommentRepliesNotificationEvent.builder()
                    .actorId(currentUser)
                    .commentId(comment.getId())
                    .recipient(posts.getAuthor().id())
                    .build());

        } else {
            eventPublisher.publishEvent(PostsCommentNotificationEvent.builder()
                    .postsId(postId)
                    .actorId(currentUser)
                    .commentId(comment.getId())
                    .recipient(posts.getAuthor().id())
                    .build());
        }


        return commentQuery.getByCommentId(comment.getId(), currentUser)
                .orElseThrow(() -> new NotFoundException("comment was not found!"));
    }

    public List<CommentResponse> getCommentResponseFromPosts(UUID postsId, UUID curr) {

        PostsResponse resp = postsService.getPostsDetail(postsId, curr);

        if (resp.getAuthor().visibility().equals(ProfilesVisibility.PRIVATE)) {
            if (followerService.isNotFollowedBy(curr, resp.getAuthor().id())) {
                throw new ForbiddenAccessExceptions("you cant comment on this posts");
            }
        }

        return commentQuery.getCommentFromPosts(postsId, curr);
    }

    public List<CommentResponse> getReplyFromComment(UUID commentId, UUID curr) {
        return commentQuery.getReplyFrom(commentId, curr);
    }

    @Transactional
    public void deleteComment(UUID commentId, UserCredentials cred) {

        DeleteCommentInfoProjection comment = commentRepository
                .findDeleteProjectionById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        UUID commentAuthorId = comment.getCommentAuthorId();
        UUID postAuthorId = comment.getPostAuthorId();

        boolean canDelete = cred.getId().equals(commentAuthorId)
                || cred.getId().equals(postAuthorId)
                || cred.getRole().equals(UserRoles.ADMIN)
                || cred.getRole().equals(UserRoles.MODERATOR);

        if (!canDelete) {
            throw new ForbiddenAccessExceptions("you don't have permission to delete this comment!");
        }

        commentRepository.deleteById(commentId);

        if (comment.getParentComment() != null) {
           commentRepository.decrementParentReplyCount(comment.getParentComment());
        }



        postsService.decreamentCommentCount(comment.getPostsId(), commentRepository.countTheReplies(comment.getCommentId()) + 1);
    }

    public PostsComment getEntity(UUID id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("comments not found!"));
    }

}
