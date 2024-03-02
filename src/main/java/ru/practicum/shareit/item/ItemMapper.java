package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
@Component
public class ItemMapper {

    public Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(null)
                .request(null)
                .build();
    }
}
