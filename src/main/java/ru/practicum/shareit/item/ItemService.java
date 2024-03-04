package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getItems(long userId);

    Item add(long userId, ItemDto itemDto);

    Item update(long userId, long itemId, ItemDto itemDto);

    Item getItemById(long userId);

    List<Item> searchItems(String searchText);
}
