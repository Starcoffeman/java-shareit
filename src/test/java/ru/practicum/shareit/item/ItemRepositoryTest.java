package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItem() {
        User owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");

        long ownerId = userRepository.save(owner).getId();

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(ownerId); // Привязываем владельца к элементу
        item.setRequestId(null);
        item.setComments(new ArrayList<>());

        // Выполняем метод, который тестируем
        Item savedItem = itemRepository.save(item);

        // Проверяем, что элемент был сохранен и его ID не пустой
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals("Test Item",itemRepository.getReferenceById(savedItem.getId()).getName());
        assertEquals("Test Description",itemRepository.getReferenceById(savedItem.getId()).getDescription());
        assertTrue(itemRepository.getReferenceById(savedItem.getId()).getAvailable());
    }

    @Test
    void findItemsByOwner() {
        User owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");

        long ownerId = userRepository.save(owner).getId();

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(ownerId);
        item.setRequestId(null);
        item.setComments(new ArrayList<>());

        // Выполняем метод, который тестируем
        Item savedItem = itemRepository.save(item);
        List<Item> findItem = itemRepository.findItemsByOwner(owner.getId());
        // Проверяем, что элемент был сохранен и его ID не пустой
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals("Test Item",findItem.get(0).getName());
        assertEquals("Test Description",findItem.get(0).getDescription());
        assertTrue(findItem.get(0).getAvailable());
        assertEquals(owner.getId(),findItem.get(0).getOwner());
    }

    @Test
    void findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue() {
        User owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");

        long ownerId = userRepository.save(owner).getId();

        Item item1 = new Item();
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setOwner(ownerId);
        item1.setRequestId(null);
        item1.setComments(new ArrayList<>());

        Item item2 = new Item();
        item2.setName("Another Item");
        item2.setDescription("Another Description");
        item2.setAvailable(true);
        item2.setOwner(ownerId);
        item2.setRequestId(null);
        item2.setComments(new ArrayList<>());

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> foundItems = itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue("test", "test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.stream().anyMatch(item -> item.getName().equalsIgnoreCase("Test Item 1")));
        assertTrue(foundItems.stream().anyMatch(item -> item.getDescription().equalsIgnoreCase("Test Description 1")));
    }

}