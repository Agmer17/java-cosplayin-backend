package cosplayin.app.profiles.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.profiles.model.dto.DetailProfileDTO;
import cosplayin.app.profiles.model.dto.ProfileUpdateDto;
import cosplayin.app.profiles.model.dto.SubmitOnBoardingRequests;
import cosplayin.app.profiles.service.ProfilesService;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class profilesController {

        private final ProfilesService service;

        @GetMapping("/me")
        @RequireAuth
        public ResponseEntity<SuccessResponse<DetailProfileDTO>> getMyProfile(@CurrentUser UserCredentials curr) {

                return ResponseEntity.ok().body(
                                SuccessResponse.<DetailProfileDTO>builder()
                                                .message("successfully retrieving your profle data")
                                                .data(service.getProfileDetails(curr.getId()))
                                                .build());
        }

        @GetMapping("/u/{username}")
        public ResponseEntity<SuccessResponse<DetailProfileDTO>> getUsersProfiles(@PathVariable String username) {

                return ResponseEntity.ok().body(
                                SuccessResponse.<DetailProfileDTO>builder()
                                                .message("successfully retrieving your profile data")
                                                .data(service.getProfileDetails(username))
                                                .build());
        }

        @PostMapping("/submit-onboarding")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ON_BOARDING })
        public ResponseEntity<SuccessResponse<DetailProfileDTO>> postOnboarding(@CurrentUser UserCredentials curr,
                        @Valid @ModelAttribute SubmitOnBoardingRequests dto) {

                DetailProfileDTO updated = service.submitOnBoarding(curr.getId(), dto);
                return ResponseEntity.ok().body(SuccessResponse.<DetailProfileDTO>builder()
                                .data(updated)
                                .message("successfully completed the onboarding process")
                                .build());
        }

        @PatchMapping("/update")
        @RequireAuth
        @RequireUserStatus({ UserStatus.ACTIVE })
        public ResponseEntity<SuccessResponse<DetailProfileDTO>> patchMyProfiles(@CurrentUser UserCredentials curr,
                        @ModelAttribute ProfileUpdateDto dto) {

                DetailProfileDTO response = service.updateProfiles(curr.getId(), dto);

                return ResponseEntity.ok().body(SuccessResponse.<DetailProfileDTO>builder()
                                .message("successfully updating your profile!")
                                .data(response)
                                .build());
        }

        @GetMapping("/discover")
        @RequireAuth
        public ResponseEntity<SuccessResponse<List<DetailProfileDTO>>> getProfileDiscover(
                        @CurrentUser UserCredentials curr) {
                return ResponseEntity.ok().body(SuccessResponse.<List<DetailProfileDTO>>builder()
                                .message("here the search result")
                                .data(service.findRandomProfile(curr.getId()))
                                .build());
        }

        @GetMapping("/search")
        public ResponseEntity<SuccessResponse<List<DetailProfileDTO>>> getSearchUsername(
                        @RequestParam(required = false, name = "q") String param) {
                return ResponseEntity.ok().body(SuccessResponse.<List<DetailProfileDTO>>builder()
                                .message("here the search result")
                                .data(service.searchByUsername(param))
                                .build());
        }

}
