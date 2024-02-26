package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.print.DocFlavor;
import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping("/{itemId}")
    public Item get(@RequestHeader("X-Sharer-User-Id") long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.update(userId,itemDto);
    }


    @GetMapping
    public List<Item> getItems() {
        return itemService.getItems();
    }

/*    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long itemId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }*/
}
