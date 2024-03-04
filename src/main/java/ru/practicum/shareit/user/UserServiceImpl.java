package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User add(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Error: Email == null");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Error: Email with out @");
        }
        return repository.add(userDto);
    }

    public User update(long id, UserDto userDto) {
        if (id < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }
        return repository.update(id, userDto);
    }

    public User getUserById(long userId) {
        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }
        return repository.getUserById(userId);
    }

    public void delete(Long userId) {
        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }
        repository.delete(userId);
    }
}
