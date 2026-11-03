package cosplayin.app.cosplaylocation.service;


import cosplayin.app.cosplayevent.model.dto.CreateCosplayEventDto;
import cosplayin.app.cosplaylocation.model.entity.CosplayEventLocations;
import cosplayin.app.cosplaylocation.repository.CosplayEventLocationRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CosplayLocationService {

    private final CosplayEventLocationRepository locationRepository;
    private final GeometryFactory geometryFactory;

    @Transactional
    public CosplayEventLocations createOrGetLocation(
            CreateCosplayEventDto dto
    ) {
        return locationRepository
                .findByOsmIdAndOsmType(dto.osmId(), dto.osmType())
                .orElseGet(() -> {
                    Point point = geometryFactory.createPoint(
                            new Coordinate(
                                    dto.longitude().doubleValue(),
                                    dto.latitude().doubleValue()
                            )
                    );

                    CosplayEventLocations location =
                            CosplayEventLocations.builder()
                                    .osmId(dto.osmId())
                                    .osmType(dto.osmType())
                                    .name(dto.locationName())
                                    .address(dto.locationAddress())
                                    .locationType(dto.locationType())
                                    .location(point)
                                    .build();

                    return locationRepository.saveAndFlush(location);
                });
    }

}
