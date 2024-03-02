package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User add(@Valid @RequestBody UserDto userDto) {
        log.info("Добавление пользователя под id: {}", userDto.getId());
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable("userId") Long userId) {
        log.info("Вывод пользователя под id: {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("Вывод всех пользователей");
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User update(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) {
        log.info("Обновлён пользователь под id: {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") Long userId) {
        log.info("Удалён пользователь под id: {}", userId);
        userService.delete(userId);
    }
}
