package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    public Item getItemById(@PathVariable("itemId") long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                       @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam("text") String searchText) {
        return itemService.searchItems(searchText);
    }
}
