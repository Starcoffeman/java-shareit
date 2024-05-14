package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.saveRequest(userId, new ItemRequestDto()));
    }

    @Test
    void saveRequestDescriptionNull() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        when(userService.getUserById(userId)).thenReturn(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setDescription(null);
        itemRequestDto.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequestDto.setItems(new ArrayList<>());

        assertThrows(ValidationException.class, () -> itemRequestService.saveRequest(userId, new ItemRequestDto()));
    }

    @Test
    void saveRequestDescriptionBlank() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("name@mail.ru");
        when(userService.getUserById(userId)).thenReturn(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setDescription(" ");
        itemRequestDto.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequestDto.setItems(new ArrayList<>());

        assertThrows(ValidationException.class, () -> itemRequestService.saveRequest(userId, new ItemRequestDto()));
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
        assertEquals("ItemRequest Description", saveRequest.getDescription());
        assertEquals(userId, saveRequest.getRequestor());
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

    @Test
    void findItemRequestsById_Success() {
        long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a laptop");
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());
        List<ItemRequest> itemRequests = Arrays.asList(itemRequest);

        when(userService.getUserById(userId)).thenReturn(user);
        when(repository.findItemRequestsByRequestor(userId)).thenReturn(itemRequests);

        List<ItemRequestDto> result = itemRequestService.findItemRequestsById(userId);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "There should be one item request");
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription(), "Descriptions should match");
        assertEquals(itemRequest.getRequestor(), result.get(0).getRequestor(), "Requestor IDs should match");
    }

    @Test
    void getRequestById_WhenRequestorNotMatch_ShouldReturnItemRequest() {
        long requestId = 1L;
        long userId = 2L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setItems(new ArrayList<>());

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(repository.findItemRequestById(requestId)).thenReturn(itemRequest);
        when(repository.findItemRequestByIdAndRequestor(requestId, userId)).thenReturn(null);

        ItemRequestDto result = itemRequestService.getRequestById(requestId, userId);

        assertNotNull(result);
        verify(repository, times(1)).findItemRequestById(requestId);
        verify(repository, times(1)).findItemRequestByIdAndRequestor(requestId, userId);
    }

    @Test
    void getAllRequests_Successful() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        UserDto user = new UserDto();
        when(userService.getUserById(userId)).thenReturn(user);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setItems(new ArrayList<>());
        itemRequest1.setDescription("Need a laptop");
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setItems(new ArrayList<>());
        itemRequest2.setDescription("Looking for a camera");
        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);
        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        when(repository.findItemRequestsByRequestorNot(eq(userId), any())).thenReturn(page);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Need a laptop", result.get(0).getDescription());
        assertEquals("Looking for a camera", result.get(1).getDescription());

        verify(userService, times(1)).getUserById(userId);
        verify(repository, times(1)).findItemRequestsByRequestorNot(eq(userId), any());
    }
}