package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    public void testMapToUserDto_NullUser() {
        User user = null;
        UserDto userDto = UserMapper.mapToUserDto(user);

        assertNull(userDto);
    }
}