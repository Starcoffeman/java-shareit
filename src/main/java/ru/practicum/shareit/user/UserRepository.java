package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User add(UserDto user);

    User getUserById(long userId);

    User update(long id, UserDto userDto);

    void delete(Long userId);
}