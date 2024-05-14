package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient {
    private static final String API_PREFIX = "/requests";
    private final RestTemplate restTemplate;

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build();
    }

    public ResponseEntity<Object> saveRequest(long userId, ItemRequestDto itemRequestDto) {
        return restTemplate.postForEntity("/", itemRequestDto, Object.class);
    }

    public ResponseEntity<Object> getAllRequests(long userId, int from, int size) {
        String url = String.format("/all?from=%d&size=%d", from, size);
        return restTemplate.getForEntity(url, Object.class);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        return restTemplate.getForEntity("/{requestId}", Object.class, requestId);
    }

    public ResponseEntity<Object> findItemRequestsById(long userId) {
        return restTemplate.getForEntity("/", Object.class);
    }
}
