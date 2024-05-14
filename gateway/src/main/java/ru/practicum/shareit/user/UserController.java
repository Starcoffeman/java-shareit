package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) {
        log.info("Forwarding request to save user {}", userDto);
        return userClient.saveUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
        log.info("Forwarding request to get user by id {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId, @RequestBody @Valid UserDto userDto) {
        log.info("Forwarding request to update user with id {} to {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") Long userId) {
        log.info("Forwarding request to delete user by id {}", userId);
        return userClient.deleteUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Forwarding request to get all users");
        return userClient.getAllUsers();
    }
}
