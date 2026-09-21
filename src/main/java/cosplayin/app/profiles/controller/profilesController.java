package cosplayin.app.profiles.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.profiles.model.projection.DetailProfileProjection;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.context.UserCredentials;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class profilesController {

    private final ProfilesService service;

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<DetailProfileProjection>> getMyProfile(@CurrentUser UserCredentials curr) {
        System.out.println("CURRENT GET MY PROFILE " + curr.getId());

        return ResponseEntity.ok().body(
                SuccessResponse.<DetailProfileProjection>builder()
                        .message("successfully retrieving your profle data")
                        .data(service.getProfileDetails(curr.getId()))
                        .build());
    }

}
