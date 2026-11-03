package cosplayin.app.cosplaylocation.repository;

import cosplayin.app.cosplaylocation.model.entity.CosplayEventLocations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CosplayEventLocationRepository extends JpaRepository<CosplayEventLocations, UUID> {
    Optional<CosplayEventLocations> findByOsmIdAndOsmType(
            Long osmId,
            String osmType
    );
}
