package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final ItemMapper itemMapper;
    private final UserRepositoryImpl userRepository;

    public List<Item> getItems(long userId) {
        return repository.getItems(userId);
    }

    public Item add(long userId, ItemDto itemDto) {
        Item item = itemMapper.toItemDto(itemDto);
        item.setOwner(userId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует поле Available");
        }

        if (userRepository.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }
        return repository.add(item);
    }

    public Item update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemMapper.toItemDto(itemDto);
        return repository.update(userId, itemId, item);
    }

    public Item getItemById(long userId) {
        return repository.getItemById(userId);
    }

    public List<Item> searchItems(String searchText) {
        if (searchText.equals("")) {
            return new ArrayList<>();
        }
        return repository.searchItems(searchText);
    }
}
