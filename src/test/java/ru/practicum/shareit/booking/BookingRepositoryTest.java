package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findBookingsByItemOwner() {
        long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findBookingsByItemOwner(ownerId, pageable);
        List<Booking> bookings = bookingsPage.getContent();

        assertEquals(0, bookings.size());
    }

    @Test
    void existsBookingsByBookerIdOrItemOwner() {
        long bookerId = 1L;
        long ownerId = 2L;
        boolean existsBookings = bookingRepository.existsBookingsByBookerIdOrItemOwner(bookerId, ownerId);

        assertFalse(existsBookings);
    }

    @Test
    void findBookingsByBookerIdOrItemOwner() {
        long bookerId = 1L;
        long ownerId = 2L;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdOrItemOwner(bookerId, ownerId, sort);

        assertEquals(0, bookings.size());
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
        long itemId = 1L;
        long bookerId = 2L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime endBefore = LocalDateTime.now();
        boolean existsBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, bookerId, status, endBefore);

        assertFalse(existsBooking);
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartIsBefore() {
        long itemId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsBefore(
                itemId, status, now, Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(null, booking);
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartIsAfter() {
        long itemId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsAfter(
                itemId, status, now, Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(null, booking);
    }

    @Test
    void findBookingsByBookerIdAndStatus() {
        long userId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStatus(
                userId, status, Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByItemOwnerAndStatus() {
        long userId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndStatus(
                userId, status, Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByItemOwnerAndStartBeforeAndEndAfter() {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndStartBeforeAndEndAfter(
                userId, now, future);

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByBookerIdAndStartBeforeAndEndAfter() {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfter(
                userId, now, future);

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByItemOwnerAndEndBeforeOrderByEndDesc() {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(userId, now);

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByBookerIdAndEndBeforeOrderByEndDesc() {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc(userId, now);

        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsByBookerId() {
        long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findBookingsByBookerId(userId, pageRequest);

        assertEquals(0, bookingsPage.getTotalElements());
    }
}