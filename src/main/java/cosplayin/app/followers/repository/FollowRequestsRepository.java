package cosplayin.app.followers.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.followers.model.entity.FollowRequests;

public interface FollowRequestsRepository extends JpaRepository<FollowRequests, UUID> {

}
