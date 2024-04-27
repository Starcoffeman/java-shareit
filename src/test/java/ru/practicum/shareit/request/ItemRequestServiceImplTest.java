package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    ItemRequestRepository repository;

    @Mock
    ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void saveRequestUserNotFound() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class,()->itemRequestService.saveRequest(userId,new ItemRequestDto()));
    }

    @Test
    void saveRequestDescriptionNull() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        when(userService.getUserById(userId)).thenReturn(user);

        ItemRequestDto itemRequestDto= new ItemRequestDto();
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setDescription(null);
        itemRequestDto.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequestDto.setItems(new ArrayList<>());

        assertThrows(ValidationException.class,()->itemRequestService.saveRequest(userId,new ItemRequestDto()));
    }

    @Test
    void saveRequestDescriptionBlank() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        when(userService.getUserById(userId)).thenReturn(user);

        ItemRequestDto itemRequestDto= new ItemRequestDto();
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setDescription(" ");
        itemRequestDto.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequestDto.setItems(new ArrayList<>());

        assertThrows(ValidationException.class,()->itemRequestService.saveRequest(userId,new ItemRequestDto()));
    }

    @Test
    void saveRequest() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName("name");
        user.setEmail("name@mail.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(userId);
        itemRequest.setDescription("ItemRequest Description");
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());

        ItemDto item = new ItemDto();
        item.setAvailable(true);
        item.setDescription("Description");
        item.setRequestId(userId);

        List<ItemDto> items = new ArrayList<>();
        items.add(item);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemService.findItemsByOwner(userId)).thenReturn(items);
        when(repository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto saveRequest = itemRequestService.saveRequest(userId, ItemRequestMapper.mapToItemRequestDto(itemRequest));

        assertNotNull(saveRequest);
        assertEquals("ItemRequest Description",saveRequest.getDescription());
        assertEquals(userId,saveRequest.getRequestor());
    }

    @Test
    void getAllRequests_UserNotFound() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getAllRequests(userId, 0, 20));
    }

    @Test
    void getAllRequests_InvalidPaginationParameters() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName("name");
        user.setEmail("name@mail.ru");
        when(userService.getUserById(userId)).thenReturn(user);

        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequests(userId, -1, 20));
        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequests(userId, 0, 0));
    }

    @Test
    void findItemRequestsById_UserNotFound() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.findItemRequestsById(userId));
    }

    @Test
    void findItemRequestsById_NoRequestsFound() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        when(userService.getUserById(userId)).thenReturn(user);
        when(repository.findItemRequestsByRequestor(userId)).thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.findItemRequestsById(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRequestById_UserNotFound() {
        long userId = 1L;
        long requestId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }

    @Test
    void getRequestById_RequestNotFound() {
        long userId = 1L;
        long requestId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        when(userService.getUserById(userId)).thenReturn(user);
        when(repository.findItemRequestById(requestId)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }

}