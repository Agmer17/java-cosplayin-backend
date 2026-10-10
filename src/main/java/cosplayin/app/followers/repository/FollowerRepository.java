package cosplayin.app.followers.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.followers.model.entity.Followers;

public interface FollowerRepository extends JpaRepository<Followers, UUID> {

}
