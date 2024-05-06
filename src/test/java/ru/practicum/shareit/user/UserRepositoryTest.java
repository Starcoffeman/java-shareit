package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setName("name");
        user.setEmail("name@email.ru");
        repository.save(user);
    }

    @Test
    void existsByEmail() {
        assertTrue(repository.existsByEmail("name@email.ru"));
    }

    @Test
    void createUser() {
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteUserById() {
        repository.deleteUserById(user.getId());
        assertTrue(repository.findAll().isEmpty());
    }
}