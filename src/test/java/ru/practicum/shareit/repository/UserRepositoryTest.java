package ru.practicum.shareit.repository;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testEmptyUsers() {
        List<User> users = repository.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    public void testOneUser() {
        User user = new User(1L, "name", "email@qwe.ru");
        repository.save(user);
        assertEquals(1, repository.findAll().size());
    }
    
    @Test
    public void testExistsByEmail() {
        String email = "test@example.com";
        boolean exists = repository.existsByEmail(email);
        assertFalse(exists);
    }


}
