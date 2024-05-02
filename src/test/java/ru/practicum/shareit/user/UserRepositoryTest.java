package ru.practicum.shareit.user;

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

    @Test
    void existsByEmail() {
        User user = new User();
        user.setName("name");
        user.setEmail("name@email.ru");
        repository.save(user);
        assertTrue(repository.existsByEmail("name@email.ru"));
    }

    @Test
    void createUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("name@email.ru");
        long id = repository.save(user).getId();
        assertEquals(1, repository.findAll().size());
        assertEquals("name", repository.getReferenceById(id).getName());
        assertEquals("name@email.ru", repository.getReferenceById(id).getEmail());
    }

    @Test
    void deleteUserById() {
        User user = new User();
        user.setName("name");
        user.setEmail("name@email.ru");
        long id = repository.save(user).getId();
        repository.deleteUserById(id);
        assertTrue(repository.findAll().isEmpty());
    }
}