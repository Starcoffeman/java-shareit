package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void mapToBookingDto_WhenBookingIsValid_ShouldReturnBookingDto() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(2L);

        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto result = BookingMapper.mapToBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
        assertEquals(booking.getItem().getId(), result.getItemId());
        assertEquals(booking.getStatus(), result.getStatus());
    }
}