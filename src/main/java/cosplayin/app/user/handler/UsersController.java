package cosplayin.app.user.handler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.core.response.SuccessResponse;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.anot.RequireAuth;
import cosplayin.app.security.anot.RequireRole;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.user.model.dto.UsernameChangeDto;
import cosplayin.app.user.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @DeleteMapping("/delete/{id}")
    @RequireAuth
    @RequireRole({ UserRoles.ADMIN })
    public ResponseEntity<SuccessResponse<String>> deleteUsers(@PathVariable UUID id,
            @CurrentUser UserCredentials ctx) {

        if (id.equals(ctx.getId())) {
            throw new ResourceConflictExceptions("you can't delete your own account!");
        }

        usersService.deleteUsers(id);
        return ResponseEntity.ok().body(SuccessResponse.<String>builder()
                .message("Successfully delete the users")
                .data(null)
                .build());
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<SuccessResponse<Boolean>> handleGetUsernameAvaibility(@PathVariable String username) {

        Boolean exist = usersService.isUsernameAvaible(username);

        return ResponseEntity.ok().body(SuccessResponse.<Boolean>builder()
                .message("successfully getting the data")
                .data(exist)
                .build());
    }

    @PatchMapping("/username")
    @RequireAuth
    @RequireUserStatus({ UserStatus.ACTIVE, UserStatus.ON_BOARDING })
    public ResponseEntity<SuccessResponse<String>> patchUsername(@Valid @RequestBody UsernameChangeDto dto,
            @CurrentUser UserCredentials curr) {
        String newUsername = usersService.updateUsername(dto.getUsername(), curr.getId());

        return ResponseEntity.ok().body(SuccessResponse.<String>builder()
                .data(newUsername)
                .message("successfully updating your username")
                .build());

    }

}
