package cosplayin.app.cosplayevent.service;

import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.cosplayevent.model.dto.CosplayEventResponseDTO;
import cosplayin.app.cosplayevent.model.dto.CreateCosplayEventDto;
import cosplayin.app.cosplayevent.model.entity.CosplayEvents;
import cosplayin.app.cosplayevent.repository.CosplayEventJdbcQuery;
import cosplayin.app.cosplayevent.repository.CosplayEventRepository;
import cosplayin.app.cosplaylocation.model.entity.CosplayEventLocations;
import cosplayin.app.cosplaylocation.service.CosplayLocationService;
import cosplayin.app.utils.storage.StorageUtils;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CosplayEventService {
    private final CosplayEventRepository repository;
    private final CosplayLocationService locationService;
    private final CosplayEventJdbcQuery eventJdbcQuery;
    private final StorageUtils storage;

    @Transactional
    public CosplayEventResponseDTO create(CreateCosplayEventDto dto) {

        FileValidationPolicy policy = new FileValidationPolicy(
                Set.of(SupportedFileType.IMAGE, SupportedFileType.VIDEO),
                10485760);

        FileModel savedMedia = storage.savePublicFile(dto.media(), policy, "event", "media");

        CosplayEventLocations location =
                locationService.createOrGetLocation(dto);

        CosplayEvents event = CosplayEvents.builder()
                .title(dto.title())
                .description(dto.description())
                .startAt(dto.startAt())
                .endAt(dto.endAt())
                .mediaUrl(this.storage.signPublicFileUrl(savedMedia.getFilePath()))
                .eventInformationUrl(dto.eventInformationUrl())
                .location(location)
                .build();

        repository.saveAndFlush(event);

        return eventJdbcQuery.findById(event.getId()).orElseThrow(() -> new NotFoundException("cosplay event data not found"));
    }

    public List<CosplayEventResponseDTO> findByBoundingBox(
            double minLon,
            double minLat,
            double maxLon,
            double maxLat,
            String title
    ) {
        return eventJdbcQuery.findByBoundingBox(
                minLon,
                minLat,
                maxLon,
                maxLat,
                title
        );
    }
}
