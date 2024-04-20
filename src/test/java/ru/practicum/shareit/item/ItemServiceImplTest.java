package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

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
    void saveItem() {
        // Подготовка тестовых данных
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());

        // Настройка мок объекта userService
        when(userService.getUserById(1L)).thenReturn(ownerDto);
        // Настройка мок объекта itemRepository
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        // Вызываем тестируемый метод
        ItemDto savedItemDto = itemService.saveItem(1L, itemDto);

        // Проверяем, что метод репозитория был вызван с правильными аргументами
        verify(itemRepository, times(1)).save(any(Item.class));
        // Проверяем, что метод сервиса пользователя был вызван для получения владельца
        verify(userService, times(1)).getUserById(1L);
        // Проверяем, что сохраненный элемент имеет непустой ID
        assertNotNull(savedItemDto.getId());
    }

    @Test
    void saveItem_negativeUserId_throwsValidationException() {
        // Подготовка тестовых данных
        UserDto ownerDto = new UserDto();
        ownerDto.setId(-1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());

        // Проверка, что выбрасывается исключение ValidationException при отрицательном userId
        assertThrows(ValidationException.class, () -> itemService.saveItem(-1L, itemDto));
    }

    @Test
    void saveItem_nullName_throwsValidationException() {
        // Подготовка тестовых данных
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName(null); // Здесь явно устанавливаем null для имени
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());

        // Проверка, что выбрасывается исключение ValidationException при отсутствии имени
        assertThrows(ValidationException.class, () -> itemService.saveItem(1L, itemDto));
    }
    @Test
    void testGetNameAuthor() {
        // Создаем мок объект Item
        Item item = new Item();
        item.setId(1L);

        // Создаем мок объекты Comment
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthorId(1L);
        comment1.setText("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthorId(2L);
        comment2.setText("Comment 2");

        // Заполняем список комментариев у объекта Item
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        item.setComments(comments);

        // Настраиваем мок объект CommentService для возвращения имени автора
        when(commentService.getNameAuthorByCommentId(1L)).thenReturn("Author 1");
        when(commentService.getNameAuthorByCommentId(2L)).thenReturn("Author 2");

        // Вызываем метод, который тестируем
        List<CommentDto> commentDtos = itemService.getNameAuthor(item);

        // Проверяем, что результат соответствует ожиданиям
        assertEquals(2, commentDtos.size());

        CommentDto commentDto1 = commentDtos.get(0);
        assertEquals("Comment 1", commentDto1.getText());
        assertEquals("Author 1", commentDto1.getAuthorName());

        CommentDto commentDto2 = commentDtos.get(1);
        assertEquals("Comment 2", commentDto2.getText());
        assertEquals("Author 2", commentDto2.getAuthorName());
    }

    @Test
    void testSearchItemsWithEmptySearchText() {
        // Подготовка тестовых данных
        String emptySearchText = "";

        // Вызываем метод, передавая пустую строку поиска
        List<ItemDto> result = itemService.searchItems(emptySearchText);

        // Проверяем, что возвращается пустой список
        assertEquals(0, result.size());
    }

    @Test
    void testSearchItemsWithBlankSearchText() {
        // Подготовка тестовых данных
        String blankSearchText = "    ";

        // Вызываем метод, передавая строку поиска, состоящую только из пробелов
        List<ItemDto> result = itemService.searchItems(blankSearchText);

        // Проверяем, что возвращается пустой список
        assertEquals(0, result.size());
    }

    @Test
    void testSearchItems() {
        // Подготовка тестовых данных
        String searchText = "test";
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("This is a test item 1");
        item1.setAvailable(true);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Another Test Item");
        item2.setDescription("This is another test item 2");
        item2.setAvailable(true);
        items.add(item2);

        // Настройка мок объекта ItemRepository для возврата тестовых данных
        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText))
                .thenReturn(items);

        // Вызываем метод, который тестируем
        List<ItemDto> result = itemService.searchItems(searchText);

        // Проверяем, что возвращаемый список содержит правильные данные
        assertEquals(2, result.size());
        assertEquals("Test Item 1", result.get(0).getName());
        assertEquals("This is a test item 1", result.get(0).getDescription());
        assertEquals("Another Test Item", result.get(1).getName());
        assertEquals("This is another test item 2", result.get(1).getDescription());
    }

    @Test
    void testAddComment() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now())).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(new UserDto(1L, "Test User", "test@example.com"));

        // Вызов метода
        CommentDto result = itemService.addComment(userId, itemId, commentText);

        // Проверка
        assertNotNull(result);
        assertEquals(commentText, result.getText());
        assertEquals("Test User", result.getAuthorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
        assertTrue(item.getComments().size() > 0);
    }



    @Test
    void testAddCommentEmptyText() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        String commentText = "";

        // Проверка на исключение, если текст комментария пустой
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void testAddCommentFutureBooking() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";

        // Мокирование поведения bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        // Проверка на исключение, если у пользователя есть будущее бронирование
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }






    @Test
    void saveItem_nullDescription_throwsValidationException() {
        // Подготовка тестовых данных
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription(null); // Здесь явно устанавливаем null для описания
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());

        // Проверка, что выбрасывается исключение ValidationException при отсутствии описания
        assertThrows(ValidationException.class, () -> itemService.saveItem(1L, itemDto));
    }

    @Test
    void findItemsByOwner() {
        // Подготовка тестовых данных
        long userId = 1L;
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // Мокирование поведения репозитория
        when(itemRepository.findItemsByOwner(userId)).thenReturn(items);

        // Вызываем тестируемый метод
        List<ItemDto> result = itemService.findItemsByOwner(userId);

        // Проверяем, что метод репозитория был вызван с правильным аргументом
        verify(itemRepository, times(1)).findItemsByOwner(userId);
        // Проверяем, что результат содержит ожидаемое количество элементов
        assertEquals(2, result.size());
        // Проверяем, что результат содержит правильные данные для каждого элемента
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
        // (Добавьте другие проверки по необходимости)
    }


    @Test
    void update() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(userId);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);

        // Мокирование поведения репозитория
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Вызываем тестируемый метод
        ItemDto updatedItemDto = itemService.update(userId, itemId, itemDto);

        // Проверяем, что метод репозитория был вызван с правильным аргументом
        verify(itemRepository, times(1)).findById(itemId);
        // Проверяем, что поля элемента были обновлены правильно
        assertEquals(itemDto.getName(), updatedItemDto.getName());
        assertEquals(itemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItemDto.getAvailable());
        // (Добавьте другие проверки по необходимости)
    }

    @Test
    void updateOwnerNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(2L); // Устанавливаем владельца, отличного от userId

        // Мокирование поведения репозитория
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Проверяем, что при попытке обновить элемент с неправильным владельцем выбрасывается исключение
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.update(userId, itemId, itemDto);
        });

        // Проверяем, что метод findById вызывался с правильным itemId
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void saveItemUserNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setRequestId(0);

        // Мокирование поведения userService, чтобы вернуть null при вызове метода getUserById
        when(userService.getUserById(anyLong())).thenReturn(null);

        // Проверяем, что при попытке сохранения элемента с несуществующим пользователем выбрасывается исключение
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.saveItem(userId, itemDto);
        });

        // Проверяем, что метод userService.getUserById вызывался с правильным userId
        verify(userService, times(1)).getUserById(userId);
    }

//    @Test
//    void getItemByIdNonOwner() {
//        long userId = 2L;
//        long itemId = 1L;
//
//        // Создание мока элемента
//        Item item = new Item();
//        item.setId(itemId);
//        item.setOwner(1L); // Пользователь, отличный от текущего пользователя
//
//        // Создание мока списка комментариев
//        List<Comment> comments = new ArrayList<>();
//        Comment comment1 = new Comment();
//        comment1.setId(1L);
//        comment1.setText("Comment 1");
//        Comment comment2 = new Comment();
//        comment2.setId(2L);
//        comment2.setText("Comment 2");
//        comments.add(comment1);
//        comments.add(comment2);
//
//        // Установка моков их репозиториев
//        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
//        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
//        when(commentService.getNameAuthorByCommentId(1L)).thenReturn("Author 1");
//        when(commentService.getNameAuthorByCommentId(2L)).thenReturn("Author 2");
//
//        // Вызов тестируемого метода
//        ItemDto itemDto = itemService.getItemById(userId, itemId);
//
//        // Проверка, что комментарии установлены, но nextBooking и lastBooking - null
//        assertEquals(null, itemDto.getNextBooking());
//        assertEquals(null, itemDto.getLastBooking());
//        assertEquals("Author 1", itemDto.getComments().get(0).getAuthorName());
//        assertEquals("Comment 1", itemDto.getComments().get(0).getText());
//        assertEquals("Author 2", itemDto.getComments().get(1).getAuthorName());
//        assertEquals("Comment 2", itemDto.getComments().get(1).getText());
//    }

    @Test
    void getItemByIdItemNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;

        // Мокирование поведения репозитория
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Проверяем, что при запросе элемента, который не найден, выбрасывается исключение
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.getItemById(userId, itemId);
        });

        // Проверяем, что метод findById вызывался с правильным itemId
        verify(itemRepository, times(1)).findById(itemId);
    }





    @Test
    void getItemById() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void addComment() {
    }
}