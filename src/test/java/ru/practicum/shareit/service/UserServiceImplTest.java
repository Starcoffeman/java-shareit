package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testGetAllUsers() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "John", "john@example.com"));
        users.add(new User(2L, "Jane", "jane@example.com"));

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDto> userDtos = userService.getAllUsers();

        // Assert
        assertEquals(2, userDtos.size());
    }

    @Test
    public void testGetUserNameById() {
        // Arrange
        long userId = 1L;
        String expectedName = "John";

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, expectedName, "john@example.com")));

        // Act
        String actualName = userService.getUserNameById(userId);

        // Assert
        assertEquals(expectedName, actualName);
    }

    @Test
    public void testGetUserById() {
        // Arrange
        long userId = 1L;
        String expectedName = "John";
        String expectedEmail = "john@example.com";

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, expectedName, expectedEmail)));

        // Act
        UserDto userDto = userService.getUserById(userId);

        // Assert
        assertNotNull(userDto);
        assertEquals(expectedName, userDto.getName());
        assertEquals(expectedEmail, userDto.getEmail());
    }

    @Test
    public void testSaveUser_WithNullName() {
        // Arrange
        UserDto userDto = new UserDto(1L, null, "john@example.com");

        // Act + Assert
        assertThrows(ValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    public void testSaveUser_WithBlankName() {
        // Arrange
        UserDto userDto = new UserDto(1L, "", "john@example.com");

        // Act + Assert
        assertThrows(ValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    public void testSaveUser_WithNullEmail() {
        // Arrange
        UserDto userDto = new UserDto(1L, "John", null);

        // Act + Assert
        assertThrows(ValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    public void testSaveUser_WithBlankEmail() {
        // Arrange
        UserDto userDto = new UserDto(1L, "John", "");

        // Act + Assert
        assertThrows(ValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    public void testUpdate_WithNullUser() {
        // Arrange
        long userId = 1L;
        UserDto updatedUserDto = new UserDto(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, updatedUserDto));
    }

    @Test
    public void testGetUserNameById_WithNonExistingUser() {
        // Arrange
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserNameById(userId));
    }

    @Test
    public void testGetUserById_WithNonExistingUser() {
        // Arrange
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void testDeleteUserById_WithNegativeUserId() {
        // Arrange
        long userId = -1L;

        // Act + Assert
        assertThrows(ValidationException.class, () -> userService.deleteUserById(userId));
    }
}
