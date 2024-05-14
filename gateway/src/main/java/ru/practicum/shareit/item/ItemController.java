package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable("itemId") long itemId) {
        log.info("Forwarding request to get item by id: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ru.practicum.shareit.item.dto.ItemDto itemDto) {
        log.info("Forwarding request to save item: {}", itemDto);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Forwarding request to update item with id: {}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Forwarding request to find items by owner");
        return itemClient.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String searchText) {
        log.info("Forwarding request to search items with text: {}", searchText);
        return itemClient.searchItems(searchText);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody String commentText) {
        log.info("Forwarding request to add comment to item with id: {}", itemId);
        return itemClient.addComment(userId, itemId, commentText);
    }
}