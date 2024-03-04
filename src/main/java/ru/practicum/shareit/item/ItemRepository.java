package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> getItems(long userId);

    Item add(Item item);

    Item getItemById(long itemId);

    Item update(long userId, long itemId, Item item);

    List<Item> searchItems(String searchText);

}
