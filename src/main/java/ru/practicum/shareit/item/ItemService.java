package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final ItemMapper itemMapper;
    private final UserRepositoryImpl userRepository;
    public List<Item> getItems() {
        return repository.getItems();
    }

    public Item add(long userId,ItemDto itemDto) {
        Item item = itemMapper.toItemDto(itemDto);
        item.setOwner(userId);
        if (itemDto.getAvailable()==null){
            throw new ValidationException("Отсутствует поле Available");
        }

        if (userRepository.getUserById(userId)==null){
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }
        return repository.add(item);
    }

    public Item update(long userId , ItemDto itemDto) {
        Item item = itemMapper.toItemDto(itemDto);
        item.setOwner(userId);
        if (userRepository.getUserById(userId)==null){
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }
        return repository.update(userId,itemDto);
    }

    public Item getItemById(long userId) {
        return repository.getItemById(userId);
    }

    public void delete(Long userId) {
        repository.delete(userId);
    }
}
