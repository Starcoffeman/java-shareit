package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest

public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testEmptyUsers() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        assertTrue(itemRequests.isEmpty());
    }

    @Test
    public void testCreateItemRequest() {
        User user = new User();
        user.setName("name634");
        user.setEmail("email@qwe.ru");
        Long id = userRepository.save(user).getId();

        User user1 = new User();
        user1.setName("arima345");
        user1.setEmail("arima@qwe.ru");
        Long id1 = userRepository.save(user1).getId();

        Item item = Item.builder()
                .name("Test899 Item")
                .description("Test899 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        itemRepository.save(item);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find test899")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        itemRequestRepository.save(itemRequest);
        assertEquals(1,itemRequestRepository.findAll().size());
    }

    @Test
    public void testFindItemRequestsByRequestor(){
        User user = new User();
        user.setName("name55");
        user.setEmail("email55@qwe.ru");
        Long id = userRepository.save(user).getId();

        User user1 = new User();
        user1.setName("arima55");
        user1.setEmail("arima55@qwe.ru");
        Long id1 = userRepository.save(user1).getId();

        Item item = Item.builder()
                .name("Test55 Item")
                .description("Test55 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        itemRepository.save(item);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Find test55")
                .requestor(id1)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .items(new ArrayList<>()).
                build();

        ItemRequest save = itemRequestRepository.save(itemRequest);
        assertEquals("Find test55",save.getDescription());
        assertEquals(id1,save.getRequestor());
    }

}
