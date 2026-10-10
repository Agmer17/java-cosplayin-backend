package cosplayin.app.likes.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.comments.service.PostsCommentService;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.likes.model.entity.CommentLikes;
import cosplayin.app.likes.repository.CommentLikesRepository;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikesService {

    private final PostsCommentService commentService;
    private final CommentLikesRepository commentLikeRepo;

    @Transactional
    public LocalDateTime createCommentsLike(UUID curr, UUID commentId) {
        PostsComment comment = commentService.getEntity(commentId);

        try {
            commentLikeRepo.saveAndFlush(CommentLikes.builder()
                    .comment(comment)
                    .user(Users.builder().id(curr).build())
                    .build());
            comment.setLikeCount(comment.getLikeCount() + 1);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictExceptions("you already liked this posts!");
        }

        return LocalDateTime.now();
    }

    @Transactional
    public void removeCommentLikes(UUID curr, UUID commentId) {

        PostsComment comment = commentService.getEntity(commentId);

        int aff = commentLikeRepo.deleteByCommentIdAndUserId(curr, commentId);

        if (aff == 0) {
            throw new NotFoundException("you haven't likes this comment");
        }
        comment.setLikeCount(comment.getLikeCount() - 1);
    }

}
