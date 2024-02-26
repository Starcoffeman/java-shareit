
package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper
@Component
public class UserMapper {
    public User toUserDto(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}

