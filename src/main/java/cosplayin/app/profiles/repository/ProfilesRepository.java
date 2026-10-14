package cosplayin.app.profiles.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.profiles.model.entity.Profiles;

public interface ProfilesRepository extends JpaRepository<Profiles, UUID> {
    @Query("""
                SELECT p
                FROM Profiles p
                WHERE p.user.username = :username
            """)
    Optional<Profiles> findByUsername(@Param("username") String username);

    @Modifying
    @Query("""
            update Profiles p
            set p.followerCount = p.followerCount + 1
            where p.id = :userId
            """)
    void incrementProfilesFollowerCount(@Param("userId") UUID userId);

    @Modifying
    @Query("""
            update Profiles p
            set p.followerCount = p.followerCount - 1
            where p.id = :userId
            """)
    void decreamentProfilesFollowerCount(@Param("userId") UUID userId);

}
