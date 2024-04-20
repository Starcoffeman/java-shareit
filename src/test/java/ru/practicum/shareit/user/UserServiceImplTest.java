package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.EnableCaching;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        // Подготовка тестовых данных
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane.doe@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.findAll())
                .thenReturn(Arrays.asList(user1, user2));

        // Выполнение метода, который тестируем
        List<UserDto> allUsers = userService.getAllUsers();

        // Проверка вызовов методов
        verify(userRepository, times(1)).findAll();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertEquals(1L, allUsers.get(0).getId());
        assertEquals("John Doe", allUsers.get(0).getName());
        assertEquals("john.doe@example.com", allUsers.get(0).getEmail());
        assertEquals(2L, allUsers.get(1).getId());
        assertEquals("Jane Doe", allUsers.get(1).getName());
        assertEquals("jane.doe@example.com", allUsers.get(1).getEmail());
    }

    @Test
    void saveUser() {
        // Подготовка тестовых данных
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    // Предположим, что сохранение пользователя возвращает пользователя с присвоенным ID
                    user.setId(1L);
                    return user;
                });

        // Выполнение метода, который тестируем
        UserDto savedUserDto = userService.saveUser(userDto);

        // Проверка вызовов методов
        verify(userRepository, times(1)).save(any(User.class));

        // Проверка возвращаемых данных
        assertNotNull(savedUserDto);
        assertNotNull(savedUserDto.getId()); // Проверка, что ID пользователя был присвоен
        assertEquals(userDto.getName(), savedUserDto.getName());
        assertEquals(userDto.getEmail(), savedUserDto.getEmail());
    }

    @Test
    void saveUserWhenNameIsNull(){
        UserDto userDto = new UserDto();
        userDto.setName(null);
        userDto.setEmail("john.doe@example.com");

        assertThrows(ValidationException.class,()-> userService.saveUser(userDto));
    }


    @Test
    void saveUserWhenNameIsBlank(){
        UserDto userDto = new UserDto();
        userDto.setName(" ");
        userDto.setEmail("john.doe@example.com");

        assertThrows(ValidationException.class,()-> userService.saveUser(userDto));
    }

    @Test
    void saveUserWhenEmailIsBlank(){
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail(" ");

        assertThrows(ValidationException.class,()-> userService.saveUser(userDto));
    }


    @Test
    void saveUserWhenEmailIsNull(){
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail(null);

        assertThrows(ValidationException.class,()-> userService.saveUser(userDto));
    }


    @Test
    void getUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        // Выполнение метода, который тестируем
        UserDto retrievedUserDto = userService.getUserById(1L);

        // Проверка вызовов методов
        verify(userRepository, times(1)).findById(1L);

        // Проверка возвращаемых данных
        assertNotNull(retrievedUserDto);
        assertEquals(1L, retrievedUserDto.getId());
        assertEquals("John Doe", retrievedUserDto.getName());
        assertEquals("john.doe@example.com", retrievedUserDto.getEmail());
    }

    @Test
    void deleteUserById() {
        // Подготовка тестовых данных
        long userId = 1L;

        // Выполнение метода, который тестируем
        userService.deleteUserById(userId);

        // Проверка вызовов методов
        verify(userRepository, times(1)).deleteUserById(userId);
    }

    @Test
    void deleteUserByIdWithNegativeId() {
        // Подготовка тестовых данных
        long userId = -1L;

        // Проверка вызовов методов
        assertThrows(ValidationException.class, () -> userService.deleteUserById(userId));
        verify(userRepository, never()).deleteUserById(anyLong());
    }
    @Test
    void update() {
        // Подготовка тестовых данных
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        User user = new User();
        user.setId(userId);
        user.setName("Original Name");
        user.setEmail("original.email@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
//        when(userRepository.updateUser(userId, userDto.getName(), userDto.getEmail()).thenReturn(user);

        // Выполнение метода, который тестируем
        UserDto updatedUserDto = userService.update(userId, userDto);

        // Проверка вызовов методов
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).updateUser(userId, userDto.getName(), userDto.getEmail());

        // Проверка возвращаемых данных
        assertNotNull(updatedUserDto);
        assertEquals(userId, updatedUserDto.getId());
        assertEquals(userDto.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    void updateWithNonExistingUser() {
        // Подготовка тестовых данных
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Проверка вызовов методов и выбрасывания исключения
        assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, userDto));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    void updateWithExistingEmail() {
        // Подготовка тестовых данных
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        User user = new User();
        user.setId(userId);
        user.setName("Original Name");
        user.setEmail("original.email@example.com");

        // Настройка поведения заглушки userRepository
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Проверка вызовов методов и выбрасывания исключения
        assertThrows(ConflictException.class, () -> userService.update(userId, userDto));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).updateUser(anyLong(), anyString(), anyString());
    }
}