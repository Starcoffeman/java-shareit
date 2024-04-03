package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{

    private final UserService userService;
    private final ItemRequestRepository repository;

    @Override
    public ItemRequestDto saveRequest(long userId, ItemRequestDto itemRequestDto) {

        if(userService.getUserById(userId)==null){
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }
        if(itemRequestDto.getDescription().isEmpty()){
            throw new ValidationException("Описание не может быть пустым");
        }
        ItemRequest itemRequest = ItemRequestMapper.mapToNewItem(itemRequestDto);
        itemRequest.setRequestor(userId);
        ItemRequest item = repository.save(itemRequest);
        ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(item);
        return dto;
    }
}
