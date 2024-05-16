package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.intf.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (!BookingState.isValid(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated(Create.class) BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setBookingApproval(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam("approved") boolean approved) {
        log.info("Updating booking status for bookingId: {}, userId: {}, approved: {}", bookingId, userId, approved);
        return bookingClient.setBookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByStateAndOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                                @Positive @RequestParam(value = "size", defaultValue = "20") int size) {
        if (!BookingState.isValid(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
        }
        log.info("Get bookings by owner with ID: {} and state: {}, from: {}, size: {}", userId, state, from, size);
        return bookingClient.findBookingsByStateAndOwnerId(userId, state, from, size);
    }
}
