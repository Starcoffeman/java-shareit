package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void saveRequest() throws Exception {
        long userId = 1L;

        // Подготовка данных для теста
        ItemRequestDto requestDto = new ItemRequestDto();
        // Заполните объект ItemRequestDto в соответствии с вашими требованиями

        // Настройка моков
        when(itemRequestService.saveRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(requestDto);

        // Вызов контроллера и проверка ответа
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Добавьте дополнительные проверки на содержание ответа, если необходимо
                .andExpect(jsonPath("$.id").exists())
                // Добавьте ожидаемое содержание ответа с помощью jsonPath
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }


    @Test
    void getAllRequests() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;

        // Подготовка моков
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        // Добавьте здесь подготовку данных для списка ItemRequestDto

        when(itemRequestService.getAllRequests(userId, from, size)).thenReturn(requestDtos);

        // Вызов контроллера и проверка ответа
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Добавьте дополнительные проверки на содержание ответа, если необходимо
                // Например, проверьте, что список requestDtos соответствует содержанию ответа
                .andExpect(jsonPath("$", hasSize(requestDtos.size())));
    }


    @Test
    void getRequestById() throws Exception {
        long userId = 1L;
        long requestId = 123L;

        // Подготовка моков
        ItemRequestDto requestDto = new ItemRequestDto();
        // Добавьте здесь подготовку данных для ItemRequestDto

        when(itemRequestService.getRequestById(requestId, userId)).thenReturn(requestDto);

        // Вызов контроллера и проверка ответа
        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Добавьте дополнительные проверки на содержание ответа, если необходимо
                // Например, проверьте, что requestDto соответствует содержанию ответа
                .andExpect(jsonPath("$.id").value(requestDto.getId()));
    }


    @Test
    void findItemRequestsById() throws Exception {
        long userId = 1L;

        // Подготовка моков
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        // Добавьте здесь подготовку данных для списка ItemRequestDto

        when(itemRequestService.findItemRequestsById(userId)).thenReturn(requestDtos);

        // Вызов контроллера и проверка ответа
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Добавьте дополнительные проверки на содержание ответа, если необходимо
                // Например, проверьте, что список requestDtos соответствует содержанию ответа
                .andExpect(jsonPath("$", hasSize(requestDtos.size())));
    }

}