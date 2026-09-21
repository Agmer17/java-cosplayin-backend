package cosplayin.app.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.user.model.entity.Users;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    boolean existsByUsername(String username);
}
