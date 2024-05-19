package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> findItemsByOwner(long userId) {
        List<Item> items = itemRepository.findItemsByOwner(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto b = ItemMapper.mapToItemDto(item);
            b.setNextBooking(findNextBookingByItemId(item.getId()));
            b.setLastBooking(findLastBookingByItemId(item.getId()));
            itemDtos.add(0, b);
        }
        itemDtos.sort(Comparator.comparingLong(ItemDto::getId));
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        if (item.getOwner() != userId) {
            throw new ResourceNotFoundException("Отсутствует user под id");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        if (item.getOwner() != userId) {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
            List<CommentDto> commentDtos = getNameAuthor(item);
            itemDto.setComments(commentDtos);
        } else {
            itemDto.setNextBooking(findNextBookingByItemId(itemId));
            itemDto.setLastBooking(findLastBookingByItemId(itemId));
            List<CommentDto> commentDtos = getNameAuthor(item);
            itemDto.setComments(commentDtos);
        }
        return itemDto;
    }

    @Override
    public List<CommentDto> getNameAuthor(Item item) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : item.getComments()) {
            CommentDto commentDto = CommentMapper.mapToCommentDto(comment);
            String authorName = commentService.getNameAuthorByCommentId(comment.getId());
            commentDto.setAuthorName(authorName);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    @Override
    @Transactional
    public ItemDto saveItem(long userId, ItemDto itemDto) {

        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }

        if (itemDto.getName() == null) {
            throw new ValidationException("Имя не может быть null");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }

        if (itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }

        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available не может быть null");
        }

        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(userId);
        if (itemDto.getRequestId() == 0) {
            item.setRequestId(null);
        } else {
            item.setRequestId(itemRequestRepository.findItemRequestByRequestor(itemDto.getRequestId()));
        }
        item = itemRepository.save(item);
        ItemDto dto = ItemMapper.mapToItemDto(item);
        dto.setNextBooking(findNextBookingByItemId(item.getId()));
        dto.setLastBooking(findLastBookingByItemId(item.getId()));
        dto.setComments(CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(item.getId())));
        if (itemDto.getRequestId() == 0) {
            dto.setRequestId(0);
        } else {
            dto.setRequestId(itemDto.getRequestId());
        }
        return dto;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText);
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, String text) {
        List<Booking> futureBookings = bookingRepository.findByBookerId(userId);

        if (!checkComment(futureBookings, itemId, userId)) {
            throw new ValidationException("User with ID " + userId + " has no approved future booking for item with ID " + itemId + ". Cannot add comment until the booking is completed.");
        }

        if (text.isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        UserDto user = userService.getUserById(userId);

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        commentRepository.save(comment);

        List<Comment> comments = item.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
        item.setComments(comments);

        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);
        commentDto.setAuthorName(user.getName());

        return commentDto;
    }

    private boolean checkComment(List<Booking> futureBookings, long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : futureBookings) {
            if (booking.getItem().getId() == itemId &&
                    booking.getItem().getOwner() != userId &&
                    booking.getStatus() == BookingStatus.APPROVED &&
                    booking.getBooker().getId() == userId &&
                    booking.getStart().isBefore(now) &&
                    booking.getEnd().isAfter(now)) {
                return true;
            }
        }
        return false;
    }

    private BookingRequestDto findLastBookingByItemId(long itemId) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        List<Booking> approvedBookings = bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.APPROVED).filter(booking -> booking.getStart().isBefore(LocalDateTime.now())).sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());

        if (!approvedBookings.isEmpty()) {
            Booking lastBooking = approvedBookings.get(0);
            BookingDto lastBookingsDTO = BookingMapper.mapToBookingDto(lastBooking);
            return BookingMapper.mapToBookingRequestDto(lastBookingsDTO);
        } else {
            return null;
        }
    }

    private BookingRequestDto findNextBookingByItemId(long itemId) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        List<Booking> approvedBookings = bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.APPROVED).filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).sorted(Comparator.comparing(Booking::getStart)).collect(Collectors.toList());

        if (!approvedBookings.isEmpty()) {
            Booking nextBooking = approvedBookings.get(0);
            BookingDto nextBookingsDTO = BookingMapper.mapToBookingDto(nextBooking);
            return BookingMapper.mapToBookingRequestDto(nextBookingsDTO);
        } else {
            return null;
        }
    }
}
