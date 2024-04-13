package ru.practicum.shareit.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;



    @Test
    public void testEmptyUsers() {
        List<User> users = repository.findAll();
        assertTrue(users.isEmpty());
        repository.deleteAll();

    }

    @Test
    public void testOneUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        repository.save(user);
        assertEquals(1, repository.findAll().size());
        repository.deleteAll();

    }

    @Test
    public void testExistsByEmail() {
        String email = "john.doe@example.com";
        boolean exists = repository.existsByEmail(email);
        assertFalse(exists);
        repository.deleteAll();

    }

    @Test
    public void testMapToUserDto(){
        User user = null;
        assertNull(UserMapper.mapToUserDto(user));
    }

    @Test
    public void testMapToUsersDto(){
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        List<User> users = new ArrayList<>();
        users.add(user);
        repository.save(user);
        List<UserDto> userDtos = UserMapper.mapToUserDto(users);
        assertEquals(users.get(0).getName(),userDtos.get(0).getName());
        assertEquals(users.get(0).getEmail(),userDtos.get(0).getEmail());
        repository.deleteAll();
    }
}
