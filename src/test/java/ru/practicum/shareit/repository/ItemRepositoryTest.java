package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {


    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testEmptyItems() {
        List<Item> items = itemRepository.findAll();
        assertTrue(items.isEmpty());
    }

    @Test
    public void testOneItem() {
        User user = new User(1L, "name", "email@qwe.ru");
        userRepository.save(user);

        Item item = new Item(1L,"item","какая-то вещь",true,1L,null,new ArrayList<>());
        itemRepository.save(item);
        assertEquals(1, itemRepository.findAll().size());
    }

}
