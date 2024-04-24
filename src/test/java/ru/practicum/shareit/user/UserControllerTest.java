package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void saveUser() throws Exception {
        User user1 = new User();
        user1.setName("name");
        user1.setEmail("name@mail.ru");

        when(userService.saveUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto user = invocationOnMock.getArgument(0);
                    return user;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"));
    }

    @Test
    void getAllUsers() throws Exception {
        // Подготовка тестовых данных
        List<UserDto> users = new ArrayList<>();
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        users.add(user);

        UserDto user1 = new UserDto();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        users.add(user1);

        // Настройка поведения заглушки userService
        when(userService.getAllUsers()).thenReturn(users);

        // Выполнение запроса на получение всех пользователей
        mvc.perform(get("/users"))
                // Проверка статуса ответа
                .andExpect(status().isOk())
                // Проверка типа контента
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Проверка тела ответа
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].email", is("name@mail.ru")))
                .andExpect(jsonPath("$[1].name", is("Alice")))
                .andExpect(jsonPath("$[1].email", is("alice@example.com")));

        // Проверка вызовов методов
        verify(userService, times(1)).getAllUsers();
    }


    @Test
    void getUserById() throws Exception {
        // Подготовка тестовых данных
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        // Настройка поведения заглушки userService
        when(userService.getUserById(1L)).thenReturn(userDto);

        // Выполнение запроса на получение пользователя по ID
        mvc.perform(get("/users/{userId}", 1L))
                // Проверка статуса ответа
                .andExpect(status().isOk())
                // Проверка типа контента
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Проверка тела ответа
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        // Проверка вызовов методов
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void update() throws Exception {
        // Подготовка тестовых данных
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        // Настройка поведения заглушки userService
        when(userService.update(1L, userDto)).thenReturn(userDto);

        // Выполнение запроса на обновление пользователя
        mvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                // Проверка статуса ответа
                .andExpect(status().isOk())
                // Проверка типа контента
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Проверка тела ответа
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        // Проверка вызовов методов
        verify(userService, times(1)).update(1L, userDto);
    }


    @Test
    void deleteUserById() throws Exception {
        UserDto userDto = new UserDto();
        long userId = 1L;
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        // Подготовка тестовых данных

        // Выполнение запроса на удаление пользователя
        mvc.perform(delete("/users/{userId}", userId))
                // Проверка статуса ответа
                .andExpect(status().isNoContent())
                // Проверка отсутствия содержимого ответа
                .andExpect(content().string("Пользователь успешно удалён"));

        verify(userService, times(1)).deleteUserById(userId);
    }

}