package cosplayin.app.bookmark.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cosplayin.app.bookmark.model.entity.PostsBookmark;
import cosplayin.app.bookmark.repository.PostsBookmarkRepository;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.posts.model.dto.PostsResponse;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.posts.service.PostsService;
import cosplayin.app.user.model.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final PostsBookmarkRepository bookmarkRepository;
    private final PostsService postsService;

    @Transactional
    public LocalDateTime createBookmark(UUID postsId, UUID curr) {
        Posts posts = postsService.findEntityById(postsId);
        Users user = Users.builder().id(curr).build();

        PostsBookmark bookmark = PostsBookmark.builder()
                .user(user)
                .posts(posts)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            bookmarkRepository.saveAndFlush(bookmark);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictExceptions("you already bookmarked this posts");
        }

        posts.setBookmarkCount(posts.getBookmarkCount() + 1);

        return LocalDateTime.now();
    }

    public List<PostsResponse> findBookmarkFromUsers(UUID id, UUID curr) {
        List<UUID> bookmarks = bookmarkRepository.getBookmarkFromUsers(id);
        List<PostsResponse> responses = postsService.findPostsDetailsInIds(bookmarks, curr);
        return responses;
    }

    @Transactional
    public void deleteBookmarks(UUID postsId, UUID userId) {
        PostsBookmark bookmark = bookmarkRepository.findByPosts_IdAndUser_Id(postsId, userId)
                .orElseThrow(() -> new NotFoundException("bookmark data not found for this posts"));

        bookmark.getPosts().setBookmarkCount(bookmark.getPosts().getBookmarkCount() - 1);
        bookmarkRepository.delete(bookmark);

    }
}
