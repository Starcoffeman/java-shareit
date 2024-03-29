package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.intf.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking saveNewBooking(@RequestHeader(USER_ID) long userId, @RequestBody @Validated(Create.class) BookingDto bookingDto) {
        log.info("Received request to save new booking for user with ID: {}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingStatus(@RequestHeader(USER_ID) long userId,
                                       @PathVariable long bookingId,
                                       @RequestParam("approved") boolean approved) {
        log.info("Received request to update booking status for user with ID: {}", userId);
        return bookingService.setBookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingByIdAndBooker(@PathVariable long bookingId, @RequestHeader(USER_ID) long userId) {
        log.info("Received request to get booking by ID {} for user with ID: {}", bookingId, userId);
        return bookingService.getBookingByIdAndBookerOrOwner(bookingId, userId);
    }

    @GetMapping("/")
    public List<Booking> getBookingsByBooker(@RequestHeader(USER_ID) long userId) {
        log.info("Received request to get bookings by booker with ID: {}", userId);
        return bookingService.findBookingsByBooker_Id(userId);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner_Id(@RequestHeader(USER_ID) long userId,
                                               @RequestParam(value = "state", required = false) String state) {
        log.info("Received request to get bookings by owner with ID: {} and state: {}", userId, state);
        if (!bookingService.existsBookingByBooker_IdOrItem_Owner(userId, userId)) {
            throw new ResourceNotFoundException("No bookings found for user with ID: " + userId);
        }

        if (state != null) {
            switch (state) {
                case "ALL":
                    log.info("Retrieving all bookings for owner with ID: {}", userId);
                    return bookingService.findBookingsByItem_Owner(userId);
                case "FUTURE":
                    log.info("Retrieving future bookings for owner with ID: {}", userId);
                    return bookingService.findBookingsByItem_Owner(userId);
                case "WAITING":
                    log.info("Retrieving waiting bookings for owner with ID: {}", userId);
                    return bookingService.findBookingsByItem_OwnerAndStatus_Waiting(userId);
                case "REJECTED":
                    log.info("Retrieving rejected bookings for owner with ID: {}", userId);
                    return bookingService.findBookingsByItem_OwnerAndStatus_Rejected(userId);
                case "CURRENT":
                    log.info("Retrieving current bookings for owner with ID: {}", userId);
                    return bookingService.findCurrentBookingsByOwner_Id(userId);
                case "PAST":
                    log.info("Retrieving past bookings for owner with ID: {}", userId);
                    return bookingService.findPastBookingsByOwner_Id(userId);
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        } else {
            log.info("No state specified. Retrieving all bookings for owner with ID: {}", userId);
            return bookingService.findBookingsByBooker_IdOrItem_Owner(userId, userId);
        }
    }

    @GetMapping
    public List<Booking> getBookingsByBooker_Id(@RequestHeader(USER_ID) long userId,
                                                @RequestParam(value = "state", required = false) String state) {
        log.info("Received request to get bookings by booker with ID: {} and state: {}", userId, state);
        if (state != null) {
            switch (state) {
                case "ALL":
                    log.info("Retrieving all bookings for booker with ID: {}", userId);
                    return bookingService.findBookingsByBooker_Id(userId);
                case "FUTURE":
                    log.info("Retrieving future bookings for booker with ID: {}", userId);
                    return bookingService.findBookingsByBooker_Id(userId);
                case "WAITING":
                    log.info("Retrieving waiting bookings for booker with ID: {}", userId);
                    return bookingService.findBookingsByBooker_IdAndStatus_Waiting(userId);
                case "REJECTED":
                    log.info("Retrieving rejected bookings for booker with ID: {}", userId);
                    return bookingService.findBookingsByBooker_IdAndStatus_Rejected(userId);
                case "PAST":
                    log.info("Retrieving past bookings for booker with ID: {}", userId);
                    return bookingService.findPastBookingsByBookerId(userId);
                case "CURRENT":
                    log.info("Retrieving current bookings for booker with ID: {}", userId);
                    return bookingService.findCurrentBookingsByBookerId(userId);
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        } else {
            log.info("No state specified. Retrieving all bookings for booker with ID: {}", userId);
            return bookingService.findBookingsByBooker_IdOrItem_Owner(userId, userId);
        }
    }
}


