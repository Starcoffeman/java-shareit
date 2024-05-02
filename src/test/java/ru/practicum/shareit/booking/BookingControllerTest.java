package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        long userId = 1L;
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.WAITING);

        Item item1 = new Item();
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setOwner(userId);
        item1.setRequestId(null);
        item1.setComments(new ArrayList<>());
        booking.setItem(item1);

        User user = new User();
        user.setId(userId);
        booking.setBooker(user);

        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.start", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{1,9}")))
                .andExpect(jsonPath("$.end", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{1,9}")));
    }

    @Test
    void setBookingApproval() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        boolean approved = true;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.WAITING);

        when(bookingService.setBookingApproval(eq(bookingId), eq(userId), eq(approved))).thenReturn(booking);

        mvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBookingByIdAndBookerOrOwner() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.WAITING);

        when(bookingService.getBookingByIdAndBookerOrOwner(eq(bookingId), eq(userId))).thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void findBookingsByBookerId() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();

        when(bookingService.findBookingsByBookerId(eq(userId), eq(from), eq(size))).thenReturn(bookings);

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findBookingsByStateAndOwnerId() throws Exception {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();

        when(bookingService.findBookingsByStateAndOwnerId(eq(userId), eq(state), eq(from), eq(size))).thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findBookingsByStateAndBookerId() throws Exception {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();

        when(bookingService.findBookingsByStateAndBookerId(eq(userId), eq(state), eq(from), eq(size))).thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}