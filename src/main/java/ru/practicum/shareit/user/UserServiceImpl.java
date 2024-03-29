package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        if (userDto.getName() == null) {
            throw new ValidationException("Имя не может быть пустым");
        }

        if (userDto.getEmail() == null) {
            throw new ValidationException("Электронная почта не может быть пустым");
        }

        if (repository.existsByEmail(userDto.getEmail())) {
            repository.save(UserMapper.mapToNewUser(userDto));
            throw new RuntimeException("Email address already exists");
        }
        User user = repository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public String getUserNameById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return user.getName();
    }

    @Override
    @Transactional
    public UserDto getUserById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User updatedUser = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        repository.updateUser(userId, updatedUser.getName(), updatedUser.getEmail());
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }
        repository.deleteUserById(userId);
    }
}
