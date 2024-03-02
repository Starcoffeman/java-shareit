package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User add(UserDto userDto) {
        if (isEmailAlreadyExists(userDto.getEmail())) {
            throw new RuntimeException("Duplicate email found");
        }
        User user = userMapper.toUserDto(userDto);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO USERS (NAME,EMAIL) VALUES (?,?)";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(sqlQuery, new String[]{"id"});
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getEmail());
                    return ps;
                },
                keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(long id) {
        String sqlQuery = "SELECT * FROM USERS WHERE ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getLong("ID"))
                    .name(userRows.getString("NAME"))
                    .email(userRows.getString("EMAIL"))
                    .build();
            log.info("Найден пользователь с id {}", id);
            return user;
        }
        log.error("Пользователь с id {} не найден", id);
        throw new ResourceNotFoundException("Пользователь не найден");
    }

    @Override
    public User update(long userId, UserDto userDto) {
        User existingUser = getUserById(userId);
        if (userDto.getEmail() != null && isEmailAlreadyExistsForOtherUser(userId, userDto.getEmail())) {
            throw new RuntimeException("Duplicate email found for another user");
        }
        if (userDto.getName() != null || userDto.getName().equals("")) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null || userDto.getName().equals("")) {
            existingUser.setEmail(userDto.getEmail());
        }
        String sqlQuery = "UPDATE USERS SET NAME = ?, EMAIL = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, existingUser.getName(), existingUser.getEmail(), userId);
        log.info("Пользователь с id {} успешно обновлен", userId);
        return getUserById(userId);
    }

    @Override
    public void delete(Long userId) {
        getUserById(userId);
        String sqlQuery = "DELETE FROM USERS WHERE ID = ?";
        int rowsAffected = jdbcTemplate.update(sqlQuery, userId);
        if (rowsAffected > 0) {
            log.info("Пользователь с id {} успешно удален", userId);
        } else {
            log.error("Не удалось найти пользователя с id {} для удаления", userId);
            throw new ResourceNotFoundException("Пользователь не найден для удаления");
        }
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .email(rs.getString("EMAIL"))
                .build();
    }

    private boolean isEmailAlreadyExists(String email) {
        String sqlQuery = "SELECT COUNT(*) FROM USERS WHERE EMAIL = ?";
        int count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, email);
        return count > 0;
    }

    private boolean isEmailAlreadyExistsForOtherUser(long userId, String email) {
        String sqlQuery = "SELECT COUNT(*) FROM USERS WHERE EMAIL = ? AND ID != ?";
        int count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, email, userId);
        return count > 0;
    }
}
