package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveRequest(@RequestHeader(USER_ID) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Saving request for user with ID {}", userId);
        return itemRequestClient.saveRequest(userId, itemRequestDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID) long userId,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "100") int size) {
        log.info("Fetching all requests for user with ID {}", userId);
        return (List<ItemRequestDto>) itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(USER_ID) long userId) {
        log.info("Fetching request with ID {} for user with ID {}", requestId, userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findItemRequestsById(@RequestHeader(USER_ID) long userId) {
        log.info("Fetching item requests for user with ID {}", userId);
        return (List<ItemRequestDto>) itemRequestClient.findItemRequestsById(userId);
    }
}
