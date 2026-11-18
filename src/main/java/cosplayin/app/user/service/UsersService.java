package cosplayin.app.user.service;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.event.UsersDeletedEvent;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository userRepo;
    private final ApplicationEventPublisher eventPublisher;

    public Users createUser(String username, UserRoles role) {
        Users user = Users.builder()
                .username(username)
                .role(role)
                .build();

        return userRepo.save(user);
    }

    public Users createUser(String username, UserRoles role, UserStatus status) {
        Users user = Users.builder()
                .username(username)
                .status(status)
                .role(role)
                .build();

        return userRepo.save(user);
    }

    public void deleteUsers(UUID id) {
        Users deletedUsers = userRepo.findById(id).orElseThrow(() -> new NotFoundException("no users found"));

        eventPublisher.publishEvent(new UsersDeletedEvent(id));
        userRepo.delete(deletedUsers);
    }

    public Boolean isUsernameAvaible(String username) {
        return userRepo.existsByUsername(username);
    }

    public String updateUsername(String newUsername, UUID curr) {
        Users user = userRepo.findById(curr).orElseThrow(() -> new NotFoundException("users id not found!"));

        if (newUsername.equals(user.getUsername())) {
            throw new RequestValidationException("username cannot be the same as before");
        }

        user.setUsername(newUsername);

        try {
            userRepo.save(user);
            return newUsername;
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictExceptions("this username already exist");
        }
    }

    public Users getUser(UUID id) {
        return userRepo.findById(id).orElse(null);
    }


    public void validateUsersExist(List<UUID> userIds) {
        List<UUID> existingIds = userRepo.findAllById(userIds)
                .stream()
                .map(Users::getId)
                .toList();

        Set<UUID> missingIds = new HashSet<>(userIds);
        existingIds.forEach(missingIds::remove);

        if (!missingIds.isEmpty()) {
            throw new NotFoundException(
                    "Users not found with IDs: " + missingIds
            );
        }
    }

}
