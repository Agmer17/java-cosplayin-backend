package cosplayin.app.profiles.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cosplayin.app.profiles.model.entity.Profiles;
import cosplayin.app.profiles.model.projection.DetailProfileProjection;

public interface ProfilesRepository extends JpaRepository<Profiles, UUID> {

    Optional<DetailProfileProjection> findProfileDetailsById(UUID id);
}
