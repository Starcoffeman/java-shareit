package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper
@Component
public class ItemMapper {

    public Item toItemDto(ItemDto itemDto) {
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
