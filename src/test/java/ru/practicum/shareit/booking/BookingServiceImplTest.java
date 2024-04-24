package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void setBookingApproval() {
        // Подготовка тестовых данных
        long userId = 1L;
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING); // Устанавливаем статус ожидания

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Item item = new Item();
        item.setOwner(userId);
        booking.setItem(item);

        // Вызываем тестируемый метод
        Booking result = bookingService.setBookingApproval(userId, bookingId, true);

        // Проверяем, что статус бронирования изменился на APPROVED
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void setBookingApprovalWaiting() {
        long userId = 1L;
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Item item = new Item();
        item.setOwner(userId);
        booking.setItem(item);

        assertThrows(ValidationException.class, () -> {
            bookingService.setBookingApproval(userId, bookingId, true);
        });

    }

    @Test
    void setBookingApprovalBookerIdNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long bookingId = 1L;

        // Мокируем вызов метода findById, чтобы он возвращал пустой Optional, как если бы бронирование не было найдено
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Проверяем, что вызывается исключение ResourceNotFoundException при попытке установить статус бронирования
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.setBookingApproval(userId, bookingId, true);
        });
    }

    @Test
    void setBookingApprovalUserNotOwnerOfItem() {
        // Подготовка тестовых данных
        long userId = 1L;
        long bookingId = 1L;

        // Создание бронирования, где владелец элемента отличается от пользователя
        Booking booking = new Booking();
        booking.setId(bookingId);
        Item item = new Item();
        item.setOwner(2L); // Устанавливаем другого пользователя в качестве владельца элемента
        booking.setItem(item);

        // Мокируем вызов метода findById, чтобы он возвращал созданное бронирование
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Проверяем, что вызывается исключение ResourceNotFoundException при попытке установить статус бронирования
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.setBookingApproval(userId, bookingId, true);
        });
    }

    @Test
    void setBookingApproval_Reject() {
        // Подготовка тестовых данных
        long userId = 1L;
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING); // Устанавливаем статус ожидания

        // Мокируем вызов метода findById
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Устанавливаем владельца предмета
        Item item = new Item();
        item.setOwner(userId);
        booking.setItem(item);

        // Вызываем тестируемый метод
        Booking result = bookingService.setBookingApproval(userId, bookingId, false);

        // Проверяем, что статус бронирования изменился на REJECTED
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void createBookingStartIsNull() {
        long userId = 1L;
        long itemId = 2L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(null);
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setItemId(itemId);
        bookingDto.setBookerId(userId);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void createBookingEndIsNull() {
        long userId = 1L;
        long itemId = 2L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(null);
        bookingDto.setItemId(itemId);
        bookingDto.setBookerId(userId);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void createBookingUserNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(false);

        // Проверка вызова метода
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        // Проверка, что метод userRepository.existsById вызывался один раз с данным userId
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void createBookingItemNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(false);

        // Проверка вызова метода
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        // Проверка, что метод itemRepository.existsById вызывался один раз с данным itemId
        verify(itemRepository, times(1)).existsById(itemId);
    }

    @Test
    void createBookingItemNotAvailable() {
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        // Настройка моков
        Item item = new Item();
        item.setAvailable(false);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        verify(itemRepository, times(1)).getById(itemId);
    }

    @Test
    void createBooking_StartTimeInPast() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        Item item = new Item();
        item.setAvailable(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        // Проверка, что метод itemRepository.getById вызывался один раз с данным itemId
        verify(itemRepository, times(1)).getById(itemId);
    }

    @Test
    void createBooking_EndTimeInPast() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().minusDays(2));

        Item item = new Item();
        item.setAvailable(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        // Проверка, что метод itemRepository.getById вызывался один раз с данным itemId
        verify(itemRepository, times(1)).getById(itemId);
    }

    @Test
    void createBooking_EndAndStartEquals() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        LocalDateTime sameTime = LocalDateTime.now().plusHours(1);
        bookingDto.setStart(sameTime);
        bookingDto.setEnd(sameTime);

        Item item = new Item();
        item.setAvailable(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });

        // Проверка, что метод itemRepository.getById вызывался один раз с данным itemId
        verify(itemRepository, times(1)).getById(itemId);
    }

    @Test
    void createBooking_UserOwnsItem() {
        // Prepare test data
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setItemId(itemId);
        bookingDto.setItemId(itemId);

        // Mock item owned by user
        Item item = new Item();
        item.setOwner(userId);
        item.setAvailable(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });
    }

    @Test
    void createBooking_Success() {
        long userId = 1L; // Change this to 2L
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        bookingDto.setItemId(itemId);
        bookingDto.setStatus(BookingStatus.WAITING);

        Item item = new Item();
        item.setOwner(userId);
        item.setAvailable(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        Booking booking = bookingService.createBooking(2L,bookingDto);

        assertEquals(start,booking.getStart());
        assertEquals(end,booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void getBookingByIdAndBookerOrOwner_BookingExistsAndUserIsOwner() {
        long bookingId = 1L;
        long userId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User user = new User();
        user.setId(userId);
        booking.setItem(new Item());
        booking.getItem().setOwner(userId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingByIdAndBookerOrOwner(bookingId, userId);

        assertEquals(booking, result);
    }

    @Test
    void getBookingByIdAndBookerOrOwner_BookingExistsAndUserIsBooker() {
        long bookingId = 1L;
        long userId = 2L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User user = new User();
        user.setId(userId);
        booking.setItem(new Item());
        booking.setBooker(user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ResourceNotFoundException.class,()-> bookingService.getBookingByIdAndBookerOrOwner(bookingId, 100L));

    }

    @Test
    void getBookingById_BookingDoesNotExist() {
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getBookingById(bookingId);
        });
    }

    @Test
    void findBookingsByBookerIdOrItemOwner() {
        long bookerId = 1L;
        long ownerId = 2L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        User user = new User();
        user.setId(bookerId);
        booking1.setBooker(user);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        Item item = new Item();
        item.setOwner(ownerId);
        booking2.setItem(item);

        List<Booking> expectedBookings = List.of(booking1, booking2);

        when(bookingRepository.findBookingsByBookerIdOrItemOwner(bookerId, ownerId, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.findBookingsByBookerIdOrItemOwner(bookerId, ownerId);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findBookingsByBookerIdAndStatusWaiting() {
        long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);

        List<Booking> expectedBookings = List.of(booking1);

        when(bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"))).thenReturn(expectedBookings);

        List<Booking> result = bookingService.findBookingsByBookerIdAndStatusWaiting(userId);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findBookingsByItemOwnerAndStatusWaiting() {
        long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);

        List<Booking> expectedBookings = List.of(booking1);

        when(bookingRepository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"))).thenReturn(expectedBookings);

        List<Booking> result = bookingService.findBookingsByItemOwnerAndStatusWaiting(userId);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findBookingsByItemOwnerAndStatusRejected() {
        long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.REJECTED);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.WAITING);

        List<Booking> expectedBookings = List.of(booking1);

        when(bookingRepository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"))).thenReturn(expectedBookings);

        List<Booking> result = bookingService.findBookingsByItemOwnerAndStatusRejected(userId);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findBookingsByBookerIdAndStatusRejected() {
        long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.REJECTED);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.WAITING);

        List<Booking> expectedBookings = List.of(booking1);

        when(bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"))).thenReturn(expectedBookings);

        List<Booking> result = bookingService.findBookingsByBookerIdAndStatusRejected(userId);

        assertEquals(expectedBookings, result);
    }

    @Test
    void existsBookingByBookerIdOrItemOwner_Exists() {
        long bookerId = 1L;
        long ownerId = 2L;

        when(bookingRepository.existsBookingsByBookerIdOrItemOwner(bookerId, ownerId)).thenReturn(true);

        assertTrue(bookingService.existsBookingByBookerIdOrItemOwner(bookerId, ownerId));
    }

    @Test
    void existsBookingByBookerIdOrItemOwner_NotExists() {
        long bookerId = 1L;
        long ownerId = 2L;

        when(bookingRepository.existsBookingsByBookerIdOrItemOwner(bookerId, ownerId)).thenReturn(false);

        assertFalse(bookingService.existsBookingByBookerIdOrItemOwner(bookerId, ownerId));
    }

    @Test
    void findBookingsByBookerId_FromInvalidPaginationParams() {
        long userId = 1L;
        int from = -1;  // Invalid value
        int size = 10;

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByBookerId(userId, from, size);
        });
    }

    @Test
    void findBookingsByBookerId_SizeInvalidPaginationParams() {
        long userId = 1L;
        int from = 0;  // Invalid value
        int size = -1;

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByBookerId(userId, from, size);
        });
    }

    @Test
    void findBookingsByBookerId_ValidInput() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        // Mocking the Page object returned by repository
        Page<Booking> page = mock(Page.class);
        when(page.getContent()).thenReturn(List.of(new Booking(), new Booking()));

        // Mocking the repository method
        when(bookingRepository.findBookingsByBookerId(eq(userId), any(Pageable.class))).thenReturn(page);

        // Call the service method
        List<Booking> bookings = bookingService.findBookingsByBookerId(userId, from, size);

        // Verify that the correct method of the repository was called
        verify(bookingRepository).findBookingsByBookerId(eq(userId), any(Pageable.class));

        // Check that the returned list of bookings is not empty
        assertFalse(bookings.isEmpty());
    }

    @Test
    void findBookingsByBookerId_NoBookingsFound() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        // Mocking the Page object returned by repository with empty content
        Page<Booking> page = mock(Page.class);
        when(page.getContent()).thenReturn(Collections.emptyList());

        // Mocking the repository method
        when(bookingRepository.findBookingsByBookerId(eq(userId), any(Pageable.class))).thenReturn(page);

        // Call the service method and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.findBookingsByBookerId(userId, from, size);
        });
    }

    @Test
    void findBookingsByItemOwner_ValidInput() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        // Mocking the Page object returned by repository
        Page<Booking> page = mock(Page.class);
        when(page.getContent()).thenReturn(List.of(new Booking(), new Booking()));

        // Mocking the repository method
        when(bookingRepository.findBookingsByItemOwner(eq(userId), any(Pageable.class))).thenReturn(page);

        // Call the service method
        List<Booking> bookings = bookingService.findBookingsByItemOwner(userId, from, size);

        // Verify that the correct method of the repository was called
        verify(bookingRepository).findBookingsByItemOwner(eq(userId), any(Pageable.class));

        // Check that the returned list of bookings is not empty
        assertFalse(bookings.isEmpty());
    }

    @Test
    void findBookingsByItemOwner_NoBookingsFound() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        // Mocking the Page object returned by repository with empty content
        Page<Booking> page = mock(Page.class);
        when(page.getContent()).thenReturn(Collections.emptyList());

        // Mocking the repository method
        when(bookingRepository.findBookingsByItemOwner(eq(userId), any(Pageable.class))).thenReturn(page);

        // Call the service method and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.findBookingsByItemOwner(userId, from, size);
        });
    }

    @Test
    void findBookingsByStateAndOwnerId_NoBookingsFound() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        when(bookingService.existsBookingByBookerIdOrItemOwner(userId, userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.findBookingsByStateAndOwnerId(userId, "ALL", from, size);
        });

    }

    @Test
    void findBookingsByStateAndOwnerId_ValidFrom() {
        long userId = 1L;
        int from = -1;
        int size = 10;

        when(bookingService.existsBookingByBookerIdOrItemOwner(userId, userId)).thenReturn(true);

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByStateAndOwnerId(userId, "ALL", from, size);
        });
    }

    @Test
    void findBookingsByStateAndOwnerId_ValidSize() {
        long userId = 1L;
        int from = 0;
        int size = -1;

        when(bookingService.existsBookingByBookerIdOrItemOwner(userId, userId)).thenReturn(true);

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByStateAndOwnerId(userId, "ALL", from, size);
        });
    }

    @Test
    void findPastBookingsByOwnerId() {
        // Подготовка тестовых данных
        long userId = 1L;
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> expectedBookings = new ArrayList<>();
        // Добавьте тестовые данные в список expectedBookings

        // Мокирование вызова метода repository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc
        when(bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(
                eq(userId), any(LocalDateTime.class)))
                .thenReturn(expectedBookings);

        // Вызов тестируемого метода
        List<Booking> result = bookingService.findPastBookingsByOwnerId(userId);

        // Проверка результатов
        assertEquals(expectedBookings, result);
    }

    @Test
    void findPastBookingsByBookerId() {
        // Подготовка тестовых данных
        long userId = 1L;
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> expectedBookings = new ArrayList<>();
        // Добавьте тестовые данные в список expectedBookings

        // Мокирование вызова метода repository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc
        when(bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc(
                eq(userId), any(LocalDateTime.class)))
                .thenReturn(expectedBookings);

        // Вызов тестируемого метода
        List<Booking> result = bookingService.findPastBookingsByBookerId(userId);

        // Проверка результатов
        assertEquals(expectedBookings, result);
    }

    @Test
    void findCurrentBookingsByOwnerId() {
        // Подготовка тестовых данных
        long userId = 1L;
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> expectedBookings = new ArrayList<>();
        // Добавьте тестовые данные в список expectedBookings

        // Мокирование вызова метода repository.findBookingsByItemOwnerAndStartBeforeAndEndAfter
        when(bookingRepository.findBookingsByItemOwnerAndStartBeforeAndEndAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedBookings);

        // Вызов тестируемого метода
        List<Booking> result = bookingService.findCurrentBookingsByOwnerId(userId);

        // Проверка результатов
        assertEquals(expectedBookings, result);
    }

    @Test
    void findCurrentBookingsByBookerId() {
        // Подготовка тестовых данных
        long userId = 1L;
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> expectedBookings = new ArrayList<>();
        // Добавьте тестовые данные в список expectedBookings

        // Мокирование вызова метода repository.findBookingsByBookerIdAndStartBeforeAndEndAfter
        when(bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedBookings);

        // Вызов тестируемого метода
        List<Booking> result = bookingService.findCurrentBookingsByBookerId(userId);

        // Проверка результатов
        assertEquals(expectedBookings, result);
    }

    @Test
    void findBookingsByStateAndBookerId_ValidFrom() {
        long userId = 1L;
        int from = -1;
        int size = 10;

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByStateAndBookerId(userId, "ALL", from, size);
        });
    }

    @Test
    void findBookingsByStateAndBookerId_ValidSize() {
        long userId = 1L;
        int from = 0;
        int size = -1;

        assertThrows(ValidationException.class, () -> {
            bookingService.findBookingsByStateAndBookerId(userId, "ALL", from, size);
        });
    }



}