package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {

    @Test
    void testMapToItemDtoNull() {
        Item item = null;
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        assertNull(itemDto);
    }

    @Test
    void testMapToItemDtoIsEmpty() {
        List<ItemDto> itemDtos = new ArrayList<>();
        List<Item> items = ItemMapper.mapToItemDto(itemDtos);
        assertEquals(new ArrayList<>(), items);
    }

    @Test
    public void testMapToItemDto_WithComments() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1);
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Test comment 1");
        item.setComments(List.of(comment1));
        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setText("Test comment 1");
        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        assertEquals(1, itemDto.getComments().size());
        assertEquals(commentDto1.getText(), itemDto.getComments().get(0).getText());
        assertEquals(commentDto1.getAuthorName(), itemDto.getComments().get(0).getAuthorName());
    }

    @Test
    public void testMapToItemDto_WithCommentsAndRequest() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Test comment 1");
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Test comment 2");
        item.setComments(List.of(comment1, comment2));

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("ItemRequest description");
        request.setRequestor(2L);
        item.setRequestId(request);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        assertEquals(2, itemDto.getComments().size());
        assertEquals(comment1.getText(), itemDto.getComments().get(0).getText());
        assertEquals(comment2.getText(), itemDto.getComments().get(1).getText());
        assertEquals(request.getRequestor(), itemDto.getRequestId());
    }
}