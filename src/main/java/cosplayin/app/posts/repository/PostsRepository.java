package cosplayin.app.posts.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.posts.model.entity.Posts;

public interface PostsRepository extends JpaRepository<Posts, UUID> {

}
