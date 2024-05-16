package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(long userId,ItemDto itemDto) {
        return post("", userId,itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> updateItem(Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, itemDto);
    }

    public ResponseEntity<Object> deleteItemById(Long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> getAllItems() {
        return get("");
    }

    public ResponseEntity<Object> searchItems(String searchText) {
        return get("/search?text=" + searchText);
    }

    public ResponseEntity<Object> addCommentToItem(Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", commentDto);
    }
}
