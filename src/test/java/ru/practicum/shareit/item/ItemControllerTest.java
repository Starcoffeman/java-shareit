package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        when(itemService.getItemById(eq(userId), eq(itemId))).thenReturn(itemDto);

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

        when(itemService.saveItem(anyLong(), any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(1);
                    return itemDto;
                });

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
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        when(itemService.update(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(updatedItemDto);

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

        when(itemService.findItemsByOwner(userId)).thenReturn(items);

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

        when(itemService.searchItems(searchText)).thenReturn(items);

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
        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);

        when(itemService.addComment(eq(userId), eq(itemId), anyString())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(commentText));
    }
}