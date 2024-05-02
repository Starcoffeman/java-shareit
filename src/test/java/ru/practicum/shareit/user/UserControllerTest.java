package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        List<UserDto> users = new ArrayList<>();
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        users.add(user);

        UserDto user1 = new UserDto();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        users.add(user1);

        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].email", is("name@mail.ru")))
                .andExpect(jsonPath("$[1].name", is("Alice")))
                .andExpect(jsonPath("$[1].email", is("alice@example.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void update() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userService.update(1L, userDto)).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).update(1L, userDto);
    }

    @Test
    void deleteUserById() throws Exception {
        UserDto userDto = new UserDto();
        long userId = 1L;
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string("Пользователь успешно удалён"));

        verify(userService, times(1)).deleteUserById(userId);
    }
}