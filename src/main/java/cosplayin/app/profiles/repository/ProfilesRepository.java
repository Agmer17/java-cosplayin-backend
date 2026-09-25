package cosplayin.app.profiles.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.entity.Profiles;

public interface ProfilesRepository extends JpaRepository<Profiles, UUID> {

        @Query("""
                        SELECT new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                            p.id, p.displayName, p.bio, p.avatarUrl, p.bannerUrl, p.visibility,
                            u.username, u.status, u.role
                        )
                        FROM Profiles p
                        JOIN p.user u
                        WHERE p.id = :id
                        """)
        Optional<DetailProfileDTO> findProfileDetailsById(UUID id);

        @Query("""
                        SELECT new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                            p.id,
                            p.displayName,
                            p.bio,
                            p.avatarUrl,
                            p.bannerUrl,
                            p.visibility,
                            u.username,
                            u.status,
                            u.role
                        )
                        FROM Profiles p
                        JOIN p.user u
                        WHERE u.username = :username
                        """)
        Optional<DetailProfileDTO> findProfileDetailsByUsername(String username);

        @Query("""
                        SELECT new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                            p.id,
                            p.displayName,
                            p.bio,
                            p.avatarUrl,
                            p.bannerUrl,
                            p.visibility,
                            u.username,
                            u.status,
                            u.role
                        )
                        FROM Profiles p
                        JOIN p.user u
                        WHERE p.id <> :currentUserId
                        ORDER BY function('random')
                        """)
        List<DetailProfileDTO> findDiscoverProfiles(
                        @Param("currentUserId") UUID currentUserId,
                        Pageable pageable);

        @Query("""
                        SELECT new cosplayin.app.profiles.model.dto.DetailProfileDTO(
                            p.id,
                            p.displayName,
                            p.bio,
                            p.avatarUrl,
                            p.bannerUrl,
                            p.visibility,
                            u.username,
                            u.status,
                            u.role
                        )
                        FROM Profiles p
                        JOIN p.user u
                        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                           OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
                        """)
        List<DetailProfileDTO> searchProfiles(
                        @Param("query") String query);

        @Query("""
                            SELECT p
                            FROM Profiles p
                            WHERE p.user.username = :username
                        """)
        Optional<Profiles> findByUsername(@Param("username") String username);

}
