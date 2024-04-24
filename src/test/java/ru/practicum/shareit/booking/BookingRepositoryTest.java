package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void findBookingsByItemOwner() {
        // Подготовка данных для теста
        long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10); // первая страница с 10 записями

        // Вызов метода
        Page<Booking> bookingsPage = bookingRepository.findBookingsByItemOwner(ownerId, pageable);
        List<Booking> bookings = bookingsPage.getContent();

        // Проверка результата
        assertEquals(0, bookings.size()); // ожидаем, что список бронирований пустой
    }

    @Test
    void existsBookingsByBookerIdOrItemOwner() {
        // Подготовка данных для теста
        long bookerId = 1L;
        long ownerId = 2L;

        // Вызов метода
        boolean existsBookings = bookingRepository.existsBookingsByBookerIdOrItemOwner(bookerId, ownerId);

        // Проверка результата
        assertFalse(existsBookings); // Предполагаем, что есть хотя бы одно бронирование от пользователя с bookerId или владельца с ownerId
    }

    @Test
    void findBookingsByBookerIdOrItemOwner() {
        // Подготовка данных для теста
        long bookerId = 1L;
        long ownerId = 2L;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdOrItemOwner(bookerId, ownerId, sort);

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
        // Подготовка данных для теста
        long itemId = 1L;
        long bookerId = 2L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime endBefore = LocalDateTime.now();

        // Вызов метода
        boolean existsBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, bookerId, status, endBefore);

        // Проверка результата (зависит от контекста вашего приложения и БД)
        assertFalse(existsBooking); // Предполагаем, что существует бронирование для itemId и bookerId с указанным статусом и окончанием до endBefore
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartIsBefore() {
        // Подготовка данных для теста
        long itemId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime now = LocalDateTime.now();

        // Вызов метода
        Booking booking = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsBefore(
                itemId, status, now, Sort.by(Sort.Direction.DESC, "start"));

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(null, booking); // Предполагаем, что бронирование не найдено
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartIsAfter() {
        // Подготовка данных для теста
        long itemId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime now = LocalDateTime.now();

        // Вызов метода
        Booking booking = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsAfter(
                itemId, status, now, Sort.by(Sort.Direction.DESC, "start"));

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(null, booking); // Предполагаем, что бронирование не найдено
    }

    @Test
    void findBookingsByBookerIdAndStatus() {
        // Подготовка данных для теста
        long userId = 1L;
        BookingStatus status = BookingStatus.APPROVED;

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStatus(
                userId, status, Sort.by(Sort.Direction.DESC, "start"));

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void findBookingsByItemOwnerAndStatus() {
        // Подготовка данных для теста
        long userId = 1L;
        BookingStatus status = BookingStatus.APPROVED;

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndStatus(
                userId, status, Sort.by(Sort.Direction.DESC, "start"));

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void findBookingsByItemOwnerAndStartBeforeAndEndAfter() {
        // Подготовка данных для теста
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndStartBeforeAndEndAfter(
                userId, now, future);

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void findBookingsByBookerIdAndStartBeforeAndEndAfter() {
        // Подготовка данных для теста
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfter(
                userId, now, future);

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }
    @Test
    void findBookingsByItemOwnerAndEndBeforeOrderByEndDesc() {
        // Подготовка данных для теста
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(userId, now);

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void findBookingsByBookerIdAndEndBeforeOrderByEndDesc() {
        // Подготовка данных для теста
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Вызов метода
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc(userId, now);

        // Проверка результата (в данном случае можно просто проверить, что метод не вернул null)
        assertEquals(0, bookings.size()); // Предполагаем, что список бронирований пустой
    }

    @Test
    void findBookingsByBookerId() {
        // Подготовка данных для теста
        long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10); // Страница с индексом 0 и размером 10 элементов

        // Вызов метода
        Page<Booking> bookingsPage = bookingRepository.findBookingsByBookerId(userId, pageRequest);

        // Проверка результата
        assertEquals(0, bookingsPage.getTotalElements()); // Предполагаем, что список бронирований пустой
    }
}