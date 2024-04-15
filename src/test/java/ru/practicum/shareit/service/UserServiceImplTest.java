package ru.practicum.shareit.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

//    @BeforeEach
//    public void setUp(){
//        repository.deleteAll();
//    }

    @Test
    public void testAllUsers(){
        UserDto userDto = new UserDto();
        userDto.setName("John Doe1");
        userDto.setEmail("john1.doe@example.com");

        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto);
        Long id = userService.saveUser(userDto).getId();
        assertEquals(userDtos.get(0).getName(),userService.getUserById(id).getName());
        assertEquals(userDtos.get(0).getEmail(),userService.getUserById(id).getEmail());
    }


    @Test
    public void testDeleteUserById() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe2");
        userDto.setEmail("john2.doe@example.com");
        Long id = userService.saveUser(userDto).getId();

        userService.deleteUserById(id);
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(id);
        });

    }

    @Test
    public void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe3");
        userDto.setEmail("john3.doe@example.com");
        Long id = userService.saveUser(userDto).getId();

        UserDto user = userService.getUserById(id);
        assertEquals("John Doe3", user.getName());
        assertEquals("john3.doe@example.com", user.getEmail());
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
    public void testDeleteUserByIdNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe5");
        userDto.setEmail("john5.doe@example.com");

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
    }


    @Test
    public void testUpdateUserWithIncorrectId() {
        Long userId = 100L;
        UserDto userDto = new UserDto();
        userDto.setName("Philip2");
        userDto.setEmail("Philip2.doe@example.com");
        UserDto a= userService.saveUser(userDto);
        UserDto user= userService.getUserById(a.getId());

        // When
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("qwe Doqwee1");
        updatedUserDto.setEmail("qwe1.doe@example.com");
        assertThrows(ResourceNotFoundException.class, ()-> userService.update(userId, updatedUserDto));
    }
}
