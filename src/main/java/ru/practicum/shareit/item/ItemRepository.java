package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemRepository {

    List<Item> getItems();
    Item add(Item item);

    Item getItemById(long itemId);

    Item update(long id, ItemDto itemDto);

    void delete(long itemId);

}
