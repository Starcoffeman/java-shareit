package ru.practicum.shareit.repository;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .build();

        itemRepository.save(item);
        assertEquals(1, itemRepository.findAll().size());
        userRepository.deleteAll(); // Очистить базу данных перед каждым тестом
        itemRepository.deleteAll();
    }

    @Test
    public void testFindAllItems() {
        List<Item> items = itemRepository.findAll();
        assertTrue(items.isEmpty());
        userRepository.deleteAll(); // Очистить базу данных перед каждым тестом
        itemRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .build();
        itemRepository.save(item);

        Optional<Item> optionalItem = itemRepository.findById(item.getId());
        assertEquals(item.getName(), optionalItem.get().getName());
        assertEquals(item.getDescription(), optionalItem.get().getDescription());
        assertEquals(item.getAvailable(), optionalItem.get().getAvailable());
        userRepository.deleteAll(); // Очистить базу данных перед каждым тестом
        itemRepository.deleteAll();
    }

    @Test
    public void testDeleteItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemRepository.save(item);
        itemRepository.deleteById(item.getId());
        assertTrue(itemRepository.findById(item.getId()).isEmpty());
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void testMapToItemDto() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Item item = null;
        assertNull(ItemMapper.mapToItemDto(item));
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void testMapToItemDtoEmpty() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        assertEquals(new ArrayList<>(), ItemMapper.mapToItemDto(itemRepository.findAll()));
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void testMapToItemsDto() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();
        itemRepository.save(item);
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(ItemMapper.mapToItemDto(item));
        assertEquals(itemDtoList.get(0).getName(), ItemMapper.mapToItemDto(itemRepository.findAll()).get(0).getName());
        assertEquals(itemDtoList.get(0).getDescription(), ItemMapper.mapToItemDto(itemRepository.findAll()).get(0)
                .getDescription());
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

}
