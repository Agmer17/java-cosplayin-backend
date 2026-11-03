package cosplayin.app.cosplayevent.controller;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.cosplayevent.model.dto.CosplayEventResponseDTO;
import cosplayin.app.cosplayevent.model.dto.CreateCosplayEventDto;
import cosplayin.app.cosplayevent.service.CosplayEventService;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cosplay-event")
@RequiredArgsConstructor
public class CosplayEventController {

    private final CosplayEventService service;

    @PostMapping
    @RequireAuth
    @RequireRole({UserRoles.ADMIN})
    public ResponseEntity<SuccessResponse<CosplayEventResponseDTO>> handlePostNewEvent(
            @Valid @ModelAttribute CreateCosplayEventDto dto
    ) {
        return ResponseEntity.ok().body(
                new SuccessResponse<>("successfully creating new event", service.create(dto))
        );

    }

    @GetMapping("/map")
    public ResponseEntity<SuccessResponse<List<CosplayEventResponseDTO>>> findByBoundingBox(
            @RequestParam double minLon,
            @RequestParam double minLat,
            @RequestParam double maxLon,
            @RequestParam double maxLat,
            @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok().body(new SuccessResponse<>("successfully getting the event data", service.findByBoundingBox(
                minLon,
                minLat,
                maxLon,
                maxLat,
                title
        )));
    }
}
