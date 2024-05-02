package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        ItemRequestDto requestDto = new ItemRequestDto();

        when(itemRequestService.saveRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getAllRequests() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemRequestDto> requestDtos = new ArrayList<>();

        when(itemRequestService.getAllRequests(userId, from, size)).thenReturn(requestDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(requestDtos.size())));
    }

    @Test
    void getRequestById() throws Exception {
        long userId = 1L;
        long requestId = 123L;
        ItemRequestDto requestDto = new ItemRequestDto();

        when(itemRequestService.getRequestById(requestId, userId)).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestDto.getId()));
    }

    @Test
    void findItemRequestsById() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> requestDtos = new ArrayList<>();

        when(itemRequestService.findItemRequestsById(userId)).thenReturn(requestDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(requestDtos.size())));
    }
}