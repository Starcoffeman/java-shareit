package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

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
        return repository.update(id, userDto);
    }

    public User getUserById(long userId) {
        return repository.getUserById(userId);
    }

    public void delete(Long userId) {
        repository.delete(userId);
    }
}
