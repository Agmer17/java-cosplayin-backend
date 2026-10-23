package cosplayin.app.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.auth.service.AuthService;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.security.anot.RequireAuth;
import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
        private final AuthService authService;

        @Value("${frontend.host.url}")
        private String frontendHostUrl;

        @Value("${app.domain}")
        private String appDomain;

        @GetMapping("/login/google")
        public ResponseEntity<Void> getLoginGoogleUrl() {
                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .location(URI.create(authService.getGoogleLoginUrl()))
                                .build();
        }

        @GetMapping("/login/discord")
        public ResponseEntity<Void> getLoginDiscordUrl() {
                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .location(URI.create(authService.getDiscordLoginUrl()))
                                .build();
        }

        @GetMapping("/google")
        public ResponseEntity<SuccessResponse<String>> handleGoogleCallback(@RequestParam String code) {
                String accessToken = authService.handleGoogleCallback(code);

                ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .domain(this.appDomain)
                                .maxAge(604800)
                                .sameSite("lax")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .location(URI.create(this.frontendHostUrl))
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .build();
        }

        @GetMapping("/discord")
        public ResponseEntity<SuccessResponse<String>> handleDiscordCallback(@RequestParam String code) {
                String accessToken = authService.handleDiscordCallback(code);

                ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(604800)
                                .domain(this.appDomain)
                                .sameSite("lax")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .location(URI.create(this.frontendHostUrl))
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .build();
        }

        @GetMapping("/logout")
        @RequireAuth
        public ResponseEntity<SuccessResponse<String>> handleGetLogout(
                        @CookieValue(name = "access_token") String token) {
                authService.logoutUser(token);
                ResponseCookie cookie = ResponseCookie.from("access_token", "")
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .domain(this.appDomain)
                                .sameSite("lax")
                                .build();

                return ResponseEntity.status(HttpStatus.OK)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(
                                                SuccessResponse.<String>builder()
                                                                .message("successfully logout from this account")
                                                                .data(null)
                                                                .build());
        }

}
