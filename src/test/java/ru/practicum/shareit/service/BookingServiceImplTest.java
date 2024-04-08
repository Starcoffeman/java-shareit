package ru.practicum.shareit.service;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;
    
    @Test
    public void testSetBookingApproval_BookingFoundAndNotOwner() {
        // Arrange
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        item.setOwner(2L); // Owner ID different from user ID
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.setBookingApproval(userId, bookingId, approved));
    }

    @Test
    public void testSetBookingApproval_BookingAlreadyApproved() {
        // Arrange
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);
        Item item = new Item();
        item.setOwner(userId);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act + Assert
        assertThrows(ValidationException.class, () -> bookingService.setBookingApproval(userId, bookingId, approved));
    }

    @Test
    public void testSetBookingApproval_BookingNotFound() {
        // Arrange
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.setBookingApproval(userId, bookingId, true));
    }

//    @Test
//    public void testCreateBooking_ValidBookingData() {
//        // Arrange
//        long userId = 1L;
//        long itemId = 1L;
//        BookingDto bookingDto = new BookingDto();
//        bookingDto.setStart(LocalDateTime.now().plusDays(1));
//        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
//
//        when(userRepository.existsById(userId)).thenReturn(true);
//        when(itemRepository.existsById(itemId)).thenReturn(true);
//        when(itemRepository.getById(itemId)).thenReturn(new Item(itemId, "Test Item", userId, true));
//
//        // Act
//        Booking booking = bookingService.createBooking(userId, bookingDto);
//
//        // Assert
//        assertNotNull(booking);
//        assertEquals(userId, booking.getBooker().getId());
//        assertEquals(itemId, booking.getItem().getId());
//        assertEquals(BookingStatus.WAITING, booking.getStatus());
//        verify(bookingRepository).save(any(Booking.class));
//    }

    @Test
    public void testCreateBooking_NullStart() {
        // Arrange
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        // Act + Assert
        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    public void testCreateBooking_NullEnd() {
        // Arrange
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));

        // Act + Assert
        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    public void testCreateBooking_UserNotFound() {
        // Arrange
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    public void testCreateBooking_ItemNotFound() {
        // Arrange
        long userId = 1L;
        long itemId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(userId, bookingDto));
    }
}
