package ru.practicum.shareit.repository;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
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

    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void testSaveItem() {
        User user = new User();
        user.setName("name12");
        user.setEmail("email12@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test1 Item")
                .description("Test1 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .build();

        itemRepository.save(item);
        assertEquals(1, itemRepository.findAll().size());
    }

    @Test
    public void testFindAllItems() {
        List<Item> items = itemRepository.findAll();
        assertTrue(items.isEmpty());
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setName("name213");
        user.setEmail("ema213il@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test2 Item")
                .description("Test2 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .build();
        itemRepository.save(item);

        Optional<Item> optionalItem = itemRepository.findById(item.getId());
        assertEquals(item.getName(), optionalItem.get().getName());
        assertEquals(item.getDescription(), optionalItem.get().getDescription());
        assertEquals(item.getAvailable(), optionalItem.get().getAvailable());
    }

    @Test
    public void testDeleteItem() {
        User user = new User();
        user.setName("name312");
        user.setEmail("email312@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test3 Item")
                .description("Test3 Description")
                .available(true)
                .owner(id)
                .requestId(null)
                .comments(new ArrayList<>())
                .build();

        itemRepository.save(item);
        itemRepository.deleteById(item.getId());
        assertTrue(itemRepository.findById(item.getId()).isEmpty());
    }

    @Test
    public void testMapToItemDto() {
        User user = new User();
        user.setName("name124");
        user.setEmail("email124@qwe.ru");
        Item item = null;
        assertNull(ItemMapper.mapToItemDto(item));

    }

    @Test
    public void testMapToItemDtoEmpty() {
        User user = new User();
        user.setName("name345");
        user.setEmail("email345@qwe.ru");
        assertEquals(new ArrayList<>(), ItemMapper.mapToItemDto(itemRepository.findAll()));
    }

    @Test
    public void testMapToItemsDto() {
        User user = new User();
        user.setName("name78");
        user.setEmail("email78@qwe.ru");
        Long id = userRepository.save(user).getId();
        Item item = Item.builder()
                .name("Test7 Item")
                .description("Test7 Description")
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
    }

}
