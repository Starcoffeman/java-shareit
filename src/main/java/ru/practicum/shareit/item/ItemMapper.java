package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Component
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getComments() != null) {
            List<CommentDto> commentDtos = CommentMapper.mapToCommentDto(item.getComments());
            itemDto.setComments(commentDtos);
        }

        if(item.getRequestId()!=null){
            itemDto.setRequestId(item.getRequestId().getRequestor());
        }

        return itemDto;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(mapToItemDto(item));
        }

        return result;
    }

    public static List<Item> mapToItemDto(List<ItemDto> items) {
        List<Item> result = new ArrayList<>();
        if(items.isEmpty()){
            return new ArrayList<>();
        }

        for (ItemDto item : items) {
            result.add(mapToNewItem(item));
        }

        return result;
    }

    public static Item mapToNewItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(0);
        return item;
    }
}

//package ru.practicum.shareit.item;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.comment.CommentMapper;
//import ru.practicum.shareit.comment.dto.CommentDto;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.ItemRequest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Mapper
//@Component
//public interface ItemMapper {
//
//    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);
//
//    @Mapping(source = "requestId.id", target = "requestId")
//    ItemDto mapToItemDto(Item item);
//
//    List<ItemDto> mapToItemDto(List<Item> items);
//
//    List<Item> mapToItem(List<ItemDto> items);
//
//
//    @Mapping(target = "owner", ignore = true)
//    @Mapping(target = "requestId", source = "itemDto.requestId", defaultValue = "null")
//    Item mapToNewItem(ItemDto itemDto);
//}
