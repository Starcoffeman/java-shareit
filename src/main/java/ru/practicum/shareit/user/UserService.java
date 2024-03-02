package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User add(UserDto userDto);

    User update(long id, UserDto userDto);

    User getUserById(long userId);

    void delete(Long userId);
}
