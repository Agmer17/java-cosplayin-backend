package cosplayin.app.followers.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.followers.model.entity.Followers;

public interface FollowerRepository extends JpaRepository<Followers, UUID> {

    Optional<Followers> findByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);

}
