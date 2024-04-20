package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Test
    void getItemById() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        // Создаем заглушку для возвращаемого объекта ItemDto
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        // Мокируем вызов сервиса для возврата объекта ItemDto
        when(itemService.getItemById(eq(userId), eq(itemId))).thenReturn(itemDto);

        // Выполняем запрос на получение товара по его ID
        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void saveItem() throws Exception {
        long userId = 1L;

        Item item1 = new Item();
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setOwner(userId);
        item1.setRequestId(null);
        item1.setComments(new ArrayList<>());


        // Мокируем вызов сервиса для возврата объекта ItemDto
        when(itemService.saveItem(anyLong(), any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(1);
                    return itemDto;
                });

        // Выполняем запрос на сохранение товара
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item1)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Item 1"))
                .andExpect(jsonPath("$.description").value("Test Description 1"))
                .andExpect(jsonPath("$.available").value(true));
    }


    @Test
    void update() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        // Создаем объект ItemDto для обновления
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        // Мокируем вызов сервиса для возврата обновленного объекта ItemDto
        when(itemService.update(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(updatedItemDto);

        // Выполняем запрос на обновление товара
        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void findItemsByOwner() throws Exception {
        long userId = 1L;

        // Создаем список предметов, которые будут возвращены из сервиса
        List<ItemDto> items = new ArrayList<>();
        ItemDto item1 = new ItemDto();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        items.add(item1);

        ItemDto item2 = new ItemDto();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        items.add(item2);

        // Мокируем вызов сервиса для возврата списка предметов
        when(itemService.findItemsByOwner(userId)).thenReturn(items);

        // Выполняем запрос на получение списка предметов владельца
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    void searchItems() throws Exception {
        String searchText = "test";

        // Создаем список предметов, которые будут возвращены из сервиса
        List<ItemDto> items = new ArrayList<>();
        ItemDto item1 = new ItemDto();
        item1.setName("Test Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        items.add(item1);

        ItemDto item2 = new ItemDto();
        item2.setName("Test Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        items.add(item2);

        // Мокируем вызов сервиса для возврата списка предметов по поисковому запросу
        when(itemService.searchItems(searchText)).thenReturn(items);

        // Выполняем запрос на поиск предметов по тексту
        mvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].available").value(false));
    }


    @Test
    void addComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";

        // Создаем объект комментария, который будет передан в метод сервиса
        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);

        // Мокируем вызов сервиса для возврата созданного комментария
        when(itemService.addComment(eq(userId), eq(itemId), anyString())).thenReturn(commentDto);

        // Выполняем запрос на добавление комментария к предмету
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(commentText));
    }

}