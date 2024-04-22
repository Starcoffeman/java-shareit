package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void setBookingApprovalWaiting(){
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

        assertThrows(ValidationException.class,()->bookingService.createBooking(userId,bookingDto));
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

        assertThrows(ValidationException.class,()->bookingService.createBooking(userId,bookingDto));
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
    void createBooking_UserTriesToBookOwnItem() {
        // Подготовка тестовых данных
        long userId = 1L;
        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(userId); // Устанавливаем пользователя в качестве владельца предмета

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1)); // Устанавливаем время начала бронирования
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));   // Устанавливаем время конца бронирования
        bookingDto.setItemId(itemId);  // Устанавливаем ID предмета

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);

        // Проверка вызова метода
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });
    }

    @Test
    void createBooking_StartTimeEqualsEndTime() {
        // Подготовка тестовых данных
        long userId = 1L;
        LocalDateTime currentTime = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(currentTime);  // Устанавливаем текущее время
        bookingDto.setEnd(currentTime);    // Устанавливаем текущее время
        bookingDto.setItemId(1L);

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(bookingDto.getItemId())).thenReturn(true);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });
    }

    @Test
    void createBooking_StartTimeInPast() {
        // Подготовка тестовых данных
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().minusHours(1)); // Устанавливаем время в прошлом
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));   // Устанавливаем время в будущем
        bookingDto.setItemId(1L);

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(bookingDto.getItemId())).thenReturn(true);

        // Проверка вызова метода
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(userId, bookingDto);
        });
    }

    @Test
    void createBooking_EndTimeBeforeStartTime() {
        // Подготовка тестовых данных
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setItemId(1L);

        // Настройка моков
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(bookingDto.getItemId())).thenReturn(true);

        // Проверка вызова метода
        try {
            bookingService.createBooking(userId, bookingDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // Подготовка тестовых данных
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

        // Проверка, что метод itemRepository.getById вызывался один раз с данным itemId
        verify(itemRepository, times(1)).getById(itemId);
    }



    @Test
    void getBookingByIdAndBookerOrOwner() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void findBookingsByStateAndOwnerId() {
    }

    @Test
    void findBookingsByStateAndBookerId() {
    }

    @Test
    void existsBookingByBookerIdOrItemOwner() {
    }

    @Test
    void findBookingsByItemOwner() {
    }

    @Test
    void findBookingsByBookerId() {
    }

    @Test
    void findBookingsByBookerIdOrItemOwner() {
    }

    @Test
    void findBookingsByBookerIdAndStatusWaiting() {
    }

    @Test
    void findBookingsByItemOwnerAndStatusWaiting() {
    }

    @Test
    void findBookingsByItemOwnerAndStatusRejected() {
    }

    @Test
    void findBookingsByBookerIdAndStatusRejected() {
    }
}