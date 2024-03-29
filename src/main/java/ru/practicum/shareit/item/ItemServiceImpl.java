package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public List<Item> getItems(long userId) {
        return repository.getItems(userId);
    }

    public Item add(long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует поле Available");
        }

        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }

        if (userRepository.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }
        return repository.add(item);
    }

    public Item update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }

        if (itemId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }

        return repository.update(userId, itemId, item);

    }

    public Item getItemById(long userId) {
        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }
        return repository.getItemById(userId);
    }

    public List<Item> searchItems(String searchText) {
        if (searchText.equals("")) {
            return new ArrayList<>();
        }
        return repository.searchItems(searchText);
    }
}
