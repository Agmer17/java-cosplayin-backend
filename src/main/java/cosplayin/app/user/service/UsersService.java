package cosplayin.app.user.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.exception.model.NotFoundException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository userRepo;

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

}
