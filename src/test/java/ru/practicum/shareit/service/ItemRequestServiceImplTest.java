package ru.practicum.shareit.service;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemRequestServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    public void testGetAllRequests_WithInvalidPaginationParams() {
        // Arrange
        long userId = 1L;
        int from = -1;
        int size = 0;
        when(userService.getUserById(userId)).thenReturn(new UserDto()); // Мокируем возвращаемый объект UserDto

        // Act + Assert
        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequests(userId, from, size));
    }

    @Test
    public void testSaveRequest() {
        // Arrange
        long userId = 1L;
        String description = "Request description";
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description(description)
                .build();

        // Мокируем userService, чтобы getUserById(userId) возвращал не null
        when(userService.getUserById(userId)).thenReturn(new UserDto());

        // Мокируем вызовы itemService.findItemsByOwner(userId) и repository.save(itemRequest)
        when(itemService.findItemsByOwner(userId)).thenReturn(new ArrayList<>());
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest savedRequest = invocation.getArgument(0);
            savedRequest.setId(1L); // Устанавливаем ID для сохраненного запроса
            return savedRequest;
        });

        // Act
        ItemRequestDto savedRequest = itemRequestService.saveRequest(userId, itemRequestDto);

        // Assert
        assertNotNull(savedRequest);
        assertEquals(description, savedRequest.getDescription());
        // Здесь можно добавить дополнительные проверки, если требуется
    }


    @Test
    public void testFindItemRequestsById() {
        // Arrange
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(new UserDto()); // Мокируем возвращаемый объект UserDto
        List<ItemRequest> requests = new ArrayList<>(); // Создаем пустой список запросов
        when(itemRequestRepository.findItemRequestsByRequestor(userId)).thenReturn(requests); // Устанавливаем мок для репозитория запросов
        List<ItemRequestDto> expected = new ArrayList<>(); // Ожидаемый пустой список DTO запросов

        // Act
        List<ItemRequestDto> result = itemRequestService.findItemRequestsById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result); // Проверяем, что результат совпадает с ожидаемым списком
    }


    @Test
    public void testGetRequestById_WithNonExistingRequest() {
        // Arrange
        long userId = 1L;
        long requestId = 1L;
        when(userService.getUserById(userId)).thenReturn(null);
        when(itemRequestRepository.findItemRequestById(anyLong())).thenReturn(null);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }

}