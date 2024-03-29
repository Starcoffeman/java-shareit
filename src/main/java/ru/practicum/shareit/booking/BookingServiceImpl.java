package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking setBookingApproval(long userId, long bookingId, boolean approved) {
        Booking booking = repository.getReferenceById(bookingId);
        if (booking.getItem().getOwner() == userId) {

            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new ValidationException("Dsa");
            }

            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return repository.save(booking);
        } else {
            throw new ResourceNotFoundException("dasd");
        }
    }

    @Override
    @Transactional
    public Booking createBooking(long userId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new ValidationException("Booking start time cannot be null");
        }

        if (bookingDto.getEnd() == null) {
            throw new ValidationException("Booking end time cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new ResourceNotFoundException("Item not found with ID: " + userId);
        }

        if (itemRepository.getById(bookingDto.getItemId()).getAvailable() == false) {
            throw new ValidationException("adsad");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Booking start time cannot be in the past");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Booking end time cannot be before booking start time");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Booking start time cannot be equal to booking end time");
        }

        long ownerId = itemRepository.getReferenceById(bookingDto.getItemId()).getOwner();
        if (userId == ownerId) {
            throw new ResourceNotFoundException("User cannot book own item");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(userRepository.getReferenceById(userId));
        booking.setItem(itemRepository.getReferenceById(bookingDto.getItemId()));
        booking.setStatus(BookingStatus.WAITING);
        repository.save(booking);
        return booking;
    }


    @Override
    @Transactional
    public Booking getBookingByIdAndBookerOrOwner(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getItem().getOwner() == userId) {
            return booking;
        }

        if (booking.getBooker().getId() == userId) {
            return booking;
        }

        throw new ResourceNotFoundException("Booking not found for user with ID: " + userId);
    }

    @Override
    @Transactional
    public Booking getBookingById(long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
    }

    @Override
    public List<Booking> findBookingsByBooker_Id(long userId) {
        List<Booking> bookings = repository.findBookingsByBooker_Id(userId);
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Booker ID: " + userId);
        }
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByItem_Owner(long userId) {
        List<Booking> bookings = repository.findBookingsByItem_Owner(userId);
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Owner ID: " + userId);
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return bookings;
    }

    @Override
    public boolean existsBookingByBooker_IdOrItem_Owner(long bookerId, long ownerId) {
        return repository.existsBookingsByBooker_IdOrItem_Owner(bookerId, ownerId);
    }

    @Override
    public List<Booking> findBookingsByBooker_IdOrItem_Owner(long bookerId, long ownerId) {
        List<Booking> bookings = repository.findBookingsByBooker_IdOrItem_Owner(bookerId, ownerId);
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByBooker_IdAndStatus_Waiting(long userId) {
        List<Booking> bookings = repository.findBookingsByBooker_Id(userId);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.WAITING) {
                result.add(booking);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }

    @Override
    public List<Booking> findBookingsByItem_OwnerAndStatus_Waiting(long userId) {
        List<Booking> bookings = repository.findBookingsByItem_Owner(userId);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.WAITING) {
                result.add(booking);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }

    @Override
    public List<Booking> findBookingsByItem_OwnerAndStatus_Rejected(long userId) {
        List<Booking> bookings = repository.findBookingsByItem_Owner(userId);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.REJECTED) {
                result.add(booking);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }

    @Override
    public List<Booking> findBookingsByBooker_IdAndStatus_Rejected(long userId) {
        List<Booking> bookings = repository.findBookingsByBooker_Id(userId);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.REJECTED) {
                result.add(booking);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }

    @Override
    public List<Booking> findPastBookingsByOwner_Id(long userId) {
        List<Booking> pastBookings = repository.findBookingsByItem_Owner(userId);
        LocalDateTime now = LocalDateTime.now();

        return pastBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findPastBookingsByBookerId(long userId) {
        List<Booking> pastBookings = repository.findBookingsByBooker_Id(userId);
        LocalDateTime now = LocalDateTime.now();

        return pastBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findCurrentBookingsByOwner_Id(long userId) {
        List<Booking> currentBookings = repository.findBookingsByItem_Owner(userId);
        LocalDateTime now = LocalDateTime.now();

        return currentBookings.stream()
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findCurrentBookingsByBookerId(long userId) {
        List<Booking> currentBookings = repository.findBookingsByBooker_Id(userId);
        LocalDateTime now = LocalDateTime.now();

        return currentBookings.stream()
                .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
    }
}
