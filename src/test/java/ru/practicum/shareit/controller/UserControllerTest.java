//package ru.practicum.shareit.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.practicum.shareit.user.UserController;
//import ru.practicum.shareit.user.UserMapper;
//import ru.practicum.shareit.user.UserService;
//import ru.practicum.shareit.user.UserServiceImpl;
//import ru.practicum.shareit.user.dto.UserDto;
//
//import java.nio.charset.StandardCharsets;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = UserController.class)
//public class UserControllerTest {
//
//    @MockBean
//    @Autowired
//    private UserServiceImpl userService;
//
//    @InjectMocks
//    private UserController controller;
//
//    @Autowired
//    private MockMvc mvc;
//
//    private UserDto userDto;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//
//    @BeforeEach
//    void setUp() {
//        mvc = MockMvcBuilders
//                .standaloneSetup(controller)
//                .build();
//
//        userDto = new UserDto(
//                1L,
//                "John",
//                "john.doe@mail.com"
//        );
//    }
//
//    @Test
//    public void saveUserTest() throws Exception{
//        when(controller.saveUser(userDto)).thenReturn(userDto);
//
//        mvc.perform(post("/users")
//                        .content(mapper.writeValueAsString(userDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(userDto.getName())))
//                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
//    }
//}
