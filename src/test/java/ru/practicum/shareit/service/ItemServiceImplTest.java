package ru.practicum.shareit.service;

//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.practicum.shareit.item.ItemMapper;
//import ru.practicum.shareit.item.ItemRepository;
//import ru.practicum.shareit.item.ItemService;
//import ru.practicum.shareit.item.ItemServiceImpl;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.UserMapper;
//import ru.practicum.shareit.user.UserService;
//import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//public class ItemServiceImplTest {
//
//    @Autowired
//    private ItemService itemService;
//
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void testOneItem() {
//        UserDto user = new UserDto(1L, "name", "qwe@mail.ru");
//        userService.saveUser(user);
//
//        Item item = new Item(1L, "item", "какая-то вещь", true,
//                1L, null, new ArrayList<>());
//
//        itemService.saveItem(1L, ItemMapper.mapToItemDto(item));
//        ItemDto item1 = itemService.getItemById(1L,1L);
//        assertEquals("item",item1.getName());
//        assertEquals("какая-то вещь",item1.getDescription());
//    }
//
//
//}

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testFindItemsByOwner() {
        // Arrange
        long userId = 1L;
        List<Item> items = new ArrayList<>();
        Item item = new Item(1L, "item", "description", true, userId, null, new ArrayList<>());
        items.add(item);
        when(itemRepository.findItemsByOwner(userId)).thenReturn(items);

        // Act
        List<ItemDto> itemDtos = itemService.findItemsByOwner(userId);

        // Assert
        assertFalse(itemDtos.isEmpty());
        assertEquals(1, itemDtos.size());
    }

    @Test
    public void testUpdate() {
        // Arrange
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "updatedItem", "updatedDescription", true, null, null, 0 ,new ArrayList<>());
        Item item = new Item(itemId, "item", "description", true, userId, null, new ArrayList<>());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Act
        ItemDto updatedItemDto = itemService.update(userId, itemId, itemDto);

        // Assert
        assertNotNull(updatedItemDto);
        assertEquals("updatedItem", updatedItemDto.getName());
        assertEquals("updatedDescription", updatedItemDto.getDescription());
    }

    @Test
    public void testGetItemById() {
        // Arrange
        long userId = 1L;
        long itemId = 1L;
        Item item = new Item(itemId, "item", "description", true, userId, null, new ArrayList<>());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        ItemDto itemDto = itemService.getItemById(userId, itemId);

        // Assert
        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        assertEquals("item", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
    }

    @Test
    public void testSaveItem() {
        // Arrange
        long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, null, null, 0,new ArrayList<>());
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(itemRequestRepository.findItemRequestByRequestor(anyLong())).thenReturn(null);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(1L); // Simulate ID generation by the repository
            return savedItem;
        });

        // Act
        ItemDto savedItemDto = itemService.saveItem(userId, itemDto);

        // Assert
        assertNotNull(savedItemDto);
        assertEquals(1L, savedItemDto.getId());
        assertEquals("item", savedItemDto.getName());
        assertEquals("description", savedItemDto.getDescription());
    }

    @Test
    public void testSearchItems() {
        // Arrange
        String searchText = "keyword";
        List<Item> items = new ArrayList<>();
        Item item = new Item(1L, "item", "description", true, 1L, null, new ArrayList<>());
        items.add(item);
        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText)).thenReturn(items);

        // Act
        List<ItemDto> itemDtos = itemService.searchItems(searchText);

        // Assert
        assertFalse(itemDtos.isEmpty());
        assertEquals(1, itemDtos.size());
    }

//    @Test
//    public void testAddComment() {
//        // Arrange
//        long userId = 1L;
//        long itemId = 1L;
//        String text = "Comment text";
//        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any(LocalDateTime.class))).thenReturn(true);
//        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        ItemDto itemDto = itemService.addComment(userId, itemId, text);
//
//        // Assert
//        assertNotNull(itemDto);
//        assertFalse(itemDto.getComments().isEmpty());
//        assertEquals(text, itemDto.getComments().get(0).getText());
//    }
}
