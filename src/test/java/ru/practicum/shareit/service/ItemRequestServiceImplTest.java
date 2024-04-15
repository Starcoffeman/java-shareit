package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest

public class ItemRequestServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void testCreateItemRequest() {
        User user = new User();
        user.setName("name771");
        user.setEmail("email747@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima77");
        user1.setEmail("arima77@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item77")
                .description("Test77 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find test")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        ItemRequestDto  itemRequestDto = itemRequestService.saveRequest(id1, ItemRequestMapper.mapToItemRequestDto(itemRequest));
        assertEquals("Find test",itemRequestService.getRequestById(itemRequestDto.getId(),id1).getDescription());
    }

    @Test
    public void testCreateItemRequestWithWrongUserId() {
        User user = new User();
        user.setName("name331");
        user.setEmail("email331@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima33");
        user1.setEmail("arima33@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item33")
                .description("Test33 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find test")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        assertThrows(ResourceNotFoundException.class,()->itemRequestService.saveRequest(100L,
                ItemRequestMapper.mapToItemRequestDto(itemRequest)));
    }

    @Test
    public void testCreateItemRequestWithDescriptionNull() {
        User user = new User();
        user.setName("name781");
        user.setEmail("email781@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima78");
        user1.setEmail("arima78@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item78")
                .description("Test78 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(null)
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        assertThrows(ValidationException.class,()->itemRequestService.saveRequest(id1,
                ItemRequestMapper.mapToItemRequestDto(itemRequest)));
    }

    @Test
    public void testCreateItemRequestWithDescriptionEmpty() {
        User user = new User();
        user.setName("name15");
        user.setEmail("email15@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima16");
        user1.setEmail("arima16@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item64")
                .description("Test64 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(" ")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        assertThrows(ValidationException.class,()->itemRequestService.saveRequest(id1,
                ItemRequestMapper.mapToItemRequestDto(itemRequest)));
    }



    @Test
    public void testGetAllRequestsWithWrongUserId() {
        User user = new User();
        user.setName("name1556");
        user.setEmail("email1556@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima1786");
        user1.setEmail("arima1786@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item694")
                .description("Test694 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find test972")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        itemRequestService.saveRequest(id1,ItemRequestMapper.mapToItemRequestDto(itemRequest));
        assertThrows(ResourceNotFoundException.class,()->itemRequestService.getAllRequests(100L,0,20));
    }

    @Test
    public void testGetAllRequestsWithWrongSizeAndFrom() {
        User user = new User();
        user.setName("name556");
        user.setEmail("email556@qwe.ru");
        Long id = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        User user1 = new User();
        user1.setName("arima786");
        user1.setEmail("arima786@qwe.ru");
        Long id1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        Item item = Item.builder()
                .name("Test Item94")
                .description("Test94 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        ItemDto saveItem = itemService.saveItem(id, ItemMapper.mapToItemDto(item));

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find testQWR")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();
        itemRequestService.saveRequest(id1,ItemRequestMapper.mapToItemRequestDto(itemRequest));

        assertThrows(ValidationException.class,()->itemRequestService.getAllRequests(id1,-1,0));
    }
    @Test
    public void testGetAllRequestsWithValidInputs() {
        // Создаем двух пользователей
        User user1 = new User();
        user1.setName("user1412");
        user1.setEmail("user1412@example.com");
        Long userId1 = userService.saveUser(UserMapper.mapToUserDto(user1)).getId();

        User user2 = new User();
        user2.setName("user1413");
        user2.setEmail("user1413@example.com");
        Long userId2 = userService.saveUser(UserMapper.mapToUserDto(user2)).getId();

        itemRequestRepository.deleteAll();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Request 1")
                .requestor(userId2)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>())
                .build();
        itemRequestService.saveRequest(userId2, ItemRequestMapper.mapToItemRequestDto(itemRequest2));

        // Получаем список всех заявок первого пользователя
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId1, 0, 20);

        // Проверяем, что в списке есть только заявки первого пользователя
        assertEquals(1, requests.size());
        assertEquals("Request 1", requests.get(0).getDescription());
    }

    @Test
    public void testGetAllRequestsWithInvalidUserId() {
        // Пытаемся получить заявки для несуществующего пользователя
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getAllRequests(100L, 0, 20));
    }

    @Test
    public void testGetAllRequestsWithInvalidPaginationParams() {
        // Создаем пользователя
        User user = new User();
        user.setName("user42");
        user.setEmail("user42@example.com");
        Long userId = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        // Пытаемся получить заявки с некорректными параметрами пагинации
        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequests(userId, -1, 0));
    }

    @Test
    public void testGetRequestByIdWithValidIds() {
        // Создаем пользователя
        User user = new User();
        user.setName("user156");
        user.setEmail("user156@example.com");
        Long userId = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        // Создаем заявку для этого пользователя
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request5555")
                .requestor(userId)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>())
                .build();
        ItemRequestDto savedRequest = itemRequestService.saveRequest(userId, ItemRequestMapper.mapToItemRequestDto(itemRequest));

        // Получаем заявку по ее ID и ID пользователя
        ItemRequestDto retrievedRequest = itemRequestService.getRequestById(savedRequest.getId(), userId);

        // Проверяем, что полученная заявка соответствует сохраненной
        assertEquals(savedRequest.getId(), retrievedRequest.getId());
        assertEquals(savedRequest.getDescription(), retrievedRequest.getDescription());
    }

    @Test
    public void testGetRequestByIdWithInvalidUserId() {
        // Создаем заявку
        User user = new User();
        user.setName("user1546");
        user.setEmail("user1564@example.com");
        Long userId = userService.saveUser(UserMapper.mapToUserDto(user)).getId();


        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request554")
                .requestor(userId)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>())
                .build();
        ItemRequestDto savedRequest = itemRequestService.saveRequest(userId, ItemRequestMapper.mapToItemRequestDto(itemRequest));

        // Пытаемся получить заявку с недопустимым ID пользователя
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestById(savedRequest.getId(), 100L));
    }

    @Test
    public void testGetRequestByIdWithInvalidRequestId() {
        // Создаем пользователя
        User user = new User();
        user.setName("user214");
        user.setEmail("user214@example.com");
        Long userId = userService.saveUser(UserMapper.mapToUserDto(user)).getId();

        // Пытаемся получить несуществующую заявку
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestById(100L, userId));
    }

}
