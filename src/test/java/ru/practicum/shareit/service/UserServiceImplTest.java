package ru.practicum.shareit.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDeleteUserById() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        Long id = userService.saveUser(userDto).getId();

        userService.deleteUserById(id);
        assertEquals(0,userService.getAllUsers().size());
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        Long id = userService.saveUser(userDto).getId();

        UserDto user = userService.getUserById(id);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        userRepository.deleteAll();
    }

    @Test
    public void testUserNotFound() {
        Long userId = 100L;
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    public void testOneUserWithNameNull() {
        UserDto userDto = new UserDto();
        userDto.setName(null);
        userDto.setEmail("eamil@asdas.ru");

        assertThrows(ValidationException.class, () -> {
            userService.saveUser(userDto);
        });

    }

    @Test
    public void testOneUserWithNameEmpty() {
        UserDto userDto = new UserDto();
        userDto.setName(" ");
        userDto.setEmail("eamil@asdas.ru");

        assertThrows(ValidationException.class, () -> {
            userService.saveUser(userDto);
        });
    }

    @Test
    public void testOneUserWithEmailNull() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail(null);

        assertThrows(ValidationException.class, () -> {
            userService.saveUser(userDto);
        });

    }

    @Test
    public void testOneUserWithEmailEmpty() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail(" ");

        assertThrows(ValidationException.class, () -> {
            userService.saveUser(userDto);
        });

    }

    @Test
    public void testAllUsers(){
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto);
        userService.saveUser(userDto);
        System.out.print(userService.getAllUsers());
        assertEquals(userDtos.size(),userService.getAllUsers().size());
    }



    @Test
    public void testDeleteUserByIdNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        assertThrows(ValidationException.class, ()->userService.deleteUserById(0));
    }


    @Test
    public void testUpdateUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Philip");
        userDto.setEmail("Philip.doe@example.com");
        UserDto a= userService.saveUser(userDto);
        UserDto user= userService.getUserById(a.getId());

        // When
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("qwe Doqwee");
        updatedUserDto.setEmail("qwe.doe@example.com");
        userService.update(user.getId(), updatedUserDto);

        // Then
        UserDto retrievedUserDto = userService.getUserById(a.getId());
        assertEquals("qwe Doqwee", retrievedUserDto.getName());
        assertEquals("qwe.doe@example.com", retrievedUserDto.getEmail());
        userRepository.deleteAll();
    }


    @Test
    public void testUpdateUserWithIncorrectId() {
        Long userId = 100L;
        UserDto userDto = new UserDto();
        userDto.setName("Philip");
        userDto.setEmail("Philip.doe@example.com");
        UserDto a= userService.saveUser(userDto);
        UserDto user= userService.getUserById(a.getId());

        // When
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("qwe Doqwee");
        updatedUserDto.setEmail("qwe.doe@example.com");
        assertThrows(ResourceNotFoundException.class, ()-> userService.update(userId, updatedUserDto));
    }

}
