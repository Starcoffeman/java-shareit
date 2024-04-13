package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
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
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        ItemDto itemDto = itemService.getItemById(id, saveItem.getId());
        assertEquals("Test Item", itemDto.getName());
        assertEquals("Test Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateItemWithWrongUserId() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ValidationException.class, () -> itemService.saveItem(0, ItemMapper.mapToItemDto(item)));

        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateItemWithWrongName() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name(null)
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ValidationException.class, () -> itemService.saveItem(id, ItemMapper.mapToItemDto(item)));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateItemWithWrongDescription() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description(null)
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ValidationException.class, () -> itemService.saveItem(id, ItemMapper.mapToItemDto(item)));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateItemNotFound() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        itemService.saveItem(id, ItemMapper.mapToItemDto(item));
        assertThrows(ResourceNotFoundException.class, () ->
                itemService.getItemById(id, 100));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateItemUserIdNotFound() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        assertThrows(ResourceNotFoundException.class, () -> itemService.saveItem(100L, ItemMapper.mapToItemDto(item)));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFindItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
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
        userRepository.deleteAll();
    }

    @Test
    public void testSearchItemsEmpty(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id,ItemMapper.mapToItemDto(item));
        assertEquals(new ArrayList<>(),itemService.searchItems(" "));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testSearchItems(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id,ItemMapper.mapToItemDto(item));
        assertEquals(1,itemService.searchItems("Description").size());
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFindItemsByOwner(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemService.saveItem(id,ItemMapper.mapToItemDto(item));
        assertEquals(1,itemService.findItemsByOwner(id).size());
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testUpdateItemWrongItemId(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id,ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item")
                .description("UPDATE Test Description")
                .available(true)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ResourceNotFoundException.class,()->itemService.update(id,100L,updateItem));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testUpdateItemWrongUserId(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id,ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item")
                .description("UPDATE Test Description")
                .available(true)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();

        assertThrows(ResourceNotFoundException.class,()->itemService.update(100L,itemDto.getId(),updateItem));
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testUpdateItem(){
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto itemDto = itemService.saveItem(id,ItemMapper.mapToItemDto(item));

        ItemDto updateItem = ItemDto.builder()
                .name("UPDATE Test Item")
                .description("UPDATE Test Description")
                .available(false)
                .nextBooking(null)
                .lastBooking(null)
                .requestId(id)
                .comments(new ArrayList<>())
                .build();


        itemService.update(id,itemDto.getId(),updateItem);
        assertEquals("UPDATE Test Item",updateItem.getName());
        assertEquals("UPDATE Test Description",updateItem.getDescription());
        assertFalse(updateItem.getAvailable());

        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
