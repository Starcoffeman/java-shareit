package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking setBookingApproval(long userId, long bookingId, boolean approved);

    Booking createBooking(long userId, BookingDto bookingDto);

    Booking getBookingByIdAndBookerOrOwner(long bookingId, long userId);

    Booking getBookingById(long bookingId);

    List<Booking> findBookingsByBookerId(long userId, int from, int size);

    boolean existsBookingByBookerIdOrItemOwner(long bookerId, long ownerId);

    List<Booking> findBookingsByBookerIdOrItemOwner(long bookerId, long ownerId);

    List<Booking> findBookingsByBookerIdAndStatusWaiting(long userId);

    List<Booking> findBookingsByItemOwnerAndStatusWaiting(long userId);

    List<Booking> findBookingsByItemOwnerAndStatusRejected(long userId);

    List<Booking> findBookingsByBookerIdAndStatusRejected(long userId);

    List<Booking> findBookingsByStateAndOwnerId(long userId, String state, int from, int size);

    List<Booking> findBookingsByStateAndBookerId(long userId, String state, int from, int size);

    List<Booking> findBookingsByItemOwner(long userId, int from, int size);
}
