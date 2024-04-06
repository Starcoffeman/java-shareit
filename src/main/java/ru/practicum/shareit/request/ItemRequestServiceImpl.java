package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository repository;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto saveRequest(long userId, ItemRequestDto itemRequestDto) {

        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        if (itemRequestDto.getDescription() == null||itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует user под id:");
        }

        ItemRequest itemRequest = ItemRequestMapper.mapToNewItem(itemRequestDto);
        itemRequest.setRequestor(userId);
        itemRequest.setItems(ItemMapper.mapToItemDto(itemService.findItemsByOwner(userId)));
        ItemRequest item = repository.save(itemRequest);
        ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(item);
        return dto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }

        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> page = repository.findItemRequestsByRequestorNot(userId,pageable);

        List<ItemRequest> requests = page.getContent();

        return ItemRequestMapper.mapToItemDto(requests);
    }


    @Override
    public List<ItemRequestDto> findItemRequestsById(long userId) {
        if (userService.getUserById(userId) == null ) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        List<ItemRequest> request = repository.findItemRequestsByRequestor(userId);
        if(request.isEmpty()){
            return new ArrayList<>();
        }

        return ItemRequestMapper.mapToItemDto(request);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        if(userService.getUserById(userId)==null){
            throw new ResourceNotFoundException("ad");
        }

        if(repository.findItemRequestById(requestId)==null){
            throw new ResourceNotFoundException("asda");
        }

        if(repository.findItemRequestByIdAndRequestor(requestId,userId)==null){
            return ItemRequestMapper.mapToItemRequestDto(repository.findItemRequestById(requestId));
        }

        return ItemRequestMapper.mapToItemRequestDto(repository.findItemRequestByIdAndRequestor(requestId,userId));
    }

}
