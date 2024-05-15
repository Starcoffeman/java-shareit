package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient {
    private static final String API_PREFIX = "/items";
    private final RestTemplate restTemplate;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        return restTemplate.getForEntity("/{itemId}", Object.class, itemId);
    }

    public ResponseEntity<Object> saveItem(long userId, ru.practicum.shareit.item.dto.ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<ru.practicum.shareit.item.dto.ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);
        return restTemplate.postForEntity("/", requestEntity, Object.class);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);
        return restTemplate.exchange("/{itemId}", HttpMethod.PATCH, requestEntity, Object.class, itemId);
    }

    public ResponseEntity<Object> findItemsByOwner(long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange("/", HttpMethod.GET, requestEntity, Object.class);
    }

    public ResponseEntity<Object> searchItems(String searchText) {
        return restTemplate.getForEntity("/search?text={searchText}", Object.class, searchText);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, String commentText) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        Map<String, Object> requestBody = Map.of("text", commentText);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForEntity("/{itemId}/comment", requestEntity, Object.class, itemId);
    }
}
