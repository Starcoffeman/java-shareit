package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingRequestDto mapToBookingRequestDto(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setId(bookingDto.getId());
        bookingRequestDto.setStart(bookingDto.getStart());
        bookingRequestDto.setEnd(bookingDto.getEnd());
        bookingRequestDto.setItemId(bookingDto.getItem().getId());
        bookingRequestDto.setBookerId(bookingDto.getBooker().getId());
        bookingRequestDto.setStatus(bookingDto.getStatus());
        return bookingRequestDto;
    }

    public static List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }
}
