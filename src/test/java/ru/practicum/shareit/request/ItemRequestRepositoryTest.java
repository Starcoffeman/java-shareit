package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findItemRequestsByRequester() {

        User user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        long userId = userRepository.save(user).getId();

        List<ItemRequest> save = new ArrayList<>();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(userId);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());
        save.add(itemRequest);
        long itemId1 = itemRequestRepository.save(itemRequest).getId();

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(userId);
        itemRequest1.setDescription("Description 1");
        itemRequest1.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest1.setItems(new ArrayList<>());
        save.add(itemRequest1);
        long itemId2 =  itemRequestRepository.save(itemRequest1).getId();

        List<ItemRequest> search = itemRequestRepository.findItemRequestsByRequestor(userId);
        assertEquals(save,search);
    }

    @Test
    void findItemRequestByRequester() {
        User user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        long userId = userRepository.save(user).getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(userId);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());
        itemRequestRepository.save(itemRequest);

        ItemRequest search = itemRequestRepository.findItemRequestByRequestor(userId);
        assertEquals("Description",search.getDescription());
        assertEquals(userId,search.getRequestor());
    }

    @Test
    void findItemRequestById() {

        User user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        long id = userRepository.save(user).getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(id);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());
        long itemId = itemRequestRepository.save(itemRequest).getId();

        ItemRequest search = itemRequestRepository.findItemRequestById(itemId);
        assertEquals("Description",search.getDescription());
        assertEquals(id,search.getRequestor());
    }

    @Test
    void findItemRequestByIdAndRequester() {
        User user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        long id = userRepository.save(user).getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(id);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        itemRequest.setItems(new ArrayList<>());
        long itemId = itemRequestRepository.save(itemRequest).getId();

        ItemRequest search = itemRequestRepository.findItemRequestByIdAndRequestor(itemId,id);
        assertEquals("Description",search.getDescription());
        assertEquals(id,search.getRequestor());
    }
}