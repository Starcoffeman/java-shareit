package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void mapToItemDto_ShouldCorrectlyMapList() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setRequestor(100L);
        request1.setCreated(java.sql.Timestamp.valueOf("2023-01-01 10:00:00"));
        request1.setItems(new ArrayList<>());

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Looking for a camera");
        request2.setRequestor(101L);
        request2.setCreated(java.sql.Timestamp.valueOf("2023-01-02 11:00:00"));
        request2.setItems(new ArrayList<>());

        List<ItemRequest> requests = Arrays.asList(request1, request2);
        List<ItemRequestDto> result = ItemRequestMapper.mapToItemDto(requests);
        assertEquals(2, result.size(), "There should be two DTOs in the result list.");
        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request1.getDescription(), result.get(0).getDescription());
        assertEquals(request1.getRequestor(), result.get(0).getRequestor());
        assertEquals(request1.getCreated(), result.get(0).getCreated());

        assertEquals(request2.getId(), result.get(1).getId());
        assertEquals(request2.getDescription(), result.get(1).getDescription());
        assertEquals(request2.getRequestor(), result.get(1).getRequestor());
        assertEquals(request2.getCreated(), result.get(1).getCreated());
    }

    @Test
    void mapToItemRequestDto_ShouldReturnNull_WhenItemRequestIsNull() {
        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(null);
        assertNull(result);
    }
}