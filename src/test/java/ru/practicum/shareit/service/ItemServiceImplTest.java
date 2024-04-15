package ru.practicum.shareit.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testCreateItem() {
        User user = new User();
        user.setName("name64");
        user.setEmail("email64@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test09 Item")
                .description("Test09 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        ItemDto itemDto = itemService.getItemById(id, saveItem.getId());
        assertEquals("Test09 Item", itemDto.getName());
        assertEquals("Test09 Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());

    }

    @Test
    public void testCreateItemWithWrongUserId() {

        User user = new User();
        user.setName("name88");
        user.setEmail("email88@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item8")
                .description("Test Description8")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ResourceNotFoundException.class, () -> itemService.saveItem(100L, ItemMapper.mapToItemDto(item)));
    }

    @Test
    public void testCreateItemWithWrongName() {
        User user = new User();
        user.setName("name51");
        user.setEmail("email51@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name(null)
                .description("Test Description145")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ValidationException.class, () -> itemService.saveItem(id, ItemMapper.mapToItemDto(item)));
    }

    @Test
    public void testCreateItemWithWrongDescription() {
        User user = new User();
        user.setName("name31");
        user.setEmail("email31@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item31")
                .description(null)
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ValidationException.class, () -> itemService.saveItem(id, ItemMapper.mapToItemDto(item)));

    }

    @Test
    public void testCreateItemNotFound() {
        User user = new User();
        user.setName("name626");
        user.setEmail("email626@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item626")
                .description("Test Description626")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        assertThrows(ResourceNotFoundException.class, () ->
                itemService.getItemById(id, 100));
    }

    @Test
    public void testCreateItemUserIdNotFound() {
        User user = new User();
        user.setName("name989");
        user.setEmail("email989@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        Item item = Item.builder()
                .name("Test Item98")
                .description("Test Description98")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        assertThrows(ResourceNotFoundException.class, () -> itemService.saveItem(100L, ItemMapper.mapToItemDto(item)));
    }

    @Test
    public void testFindItem() {
        User user = new User();
        user.setName("name07");
        user.setEmail("email07@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item07")
                .description("Test Description07")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        ItemDto getItem = itemService.getItemById(10L, saveItem.getId());
        assertEquals(null, getItem.getLastBooking());
        assertEquals(null, getItem.getNextBooking());
        itemRepository.deleteAll();
    }

    @Test
    public void testSearchItemsEmpty() {
        User user = new User();
        user.setName("name707");
        user.setEmail("email707@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item707")
                .description("Test Description707")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        assertEquals(new ArrayList<>(), itemService.searchItems(" "));
    }

    @Test
    public void testSearchItems() {
        User user = new User();
        user.setName("name58");
        user.setEmail("email58@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item58")
                .description("Test Description58")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        assertEquals(1, itemService.searchItems("Description58").size());
    }

    @Test
    public void testFindItemsByOwner() {
        User user = new User();
        user.setName("name63");
        user.setEmail("email63@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item63")
                .description("Test Description63")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        assertEquals(1, itemService.findItemsByOwner(id).size());
    }

    @Test
    public void testUpdateItemWrongItemId() {
        User user = new User();
        user.setName("name245");
        user.setEmail("email245@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item245")
                .description("Test Description245")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item")
                .description("UPDATE Test Description")
                .available(true)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ResourceNotFoundException.class, () -> itemService.update(id, 100L, updateItem));
    }

    @Test
    public void testUpdateItemWrongUserId() {
        User user = new User();
        user.setName("name776");
        user.setEmail("email776@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item776")
                .description("Test Description776")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item3")
                .description("UPDATE Test Description3")
                .available(true)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ResourceNotFoundException.class, () -> itemService.update(100L, itemDto.getId(), updateItem));
    }

    @Test
    public void testUpdateItem() {
        User user = new User();
        user.setName("name63365");
        user.setEmail("email63365@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item63365")
                .description("Test Description63365")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item6")
                .description("UPDATE Test Description6")
                .available(false)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();

        itemService.update(id, itemDto.getId(), updateItem);
        assertEquals("UPDATE Test Item6", updateItem.getName());
        assertEquals("UPDATE Test Description6", updateItem.getDescription());
        assertFalse(updateItem.getAvailable());
    }
    @Test
    public void testAddCommentWithBlankText() {
        // Создаем пользователя
        User user = new User();
        user.setName("user515123");
        user.setEmail("user515123@example.com");
        Long userId = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        // Создаем товар
        Item item = Item.builder()
                .name("Test Item 15512")
                .description("Test Description 15512")
                .available(true)
                .owner(userId)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto savedItem = itemService.saveItem(userId, ItemMapper.mapToItemDto(item));

        // Пытаемся добавить комментарий с пустым текстом
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, savedItem.getId(), " "));
    }
//    @Test
//    public void createComment(){
//        User user = new User();
//        user.setName("name64");
//        user.setEmail("email64@qwe.ru");
//        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
//        Item item = Item.builder()
//                .name("Test09 Item")
//                .description("Test09 Description")
//                .available(true)
//                .owner(id)
//                .requestId(null)
//                .comments(new ArrayList<>())
//                .build();
//        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));
//
//        Comment comment = Comment.builder()
//                .text("Random")
//                .itemId(saveItem.getId())
//                .authorId(id)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        CommentDto a = itemService.addComment(id,saveItem.getId(),"Random");
//        assertEquals("Random",a.getText());
//    }
}
