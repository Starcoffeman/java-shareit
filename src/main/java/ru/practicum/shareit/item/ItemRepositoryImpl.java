package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Item> getItems(long userId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE OWNER = ? AND AVAILABLE=TRUE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToItem, userId);
    }

    @Override
    public List<Item> searchItems(String searchText) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE LOWER(NAME) LIKE LOWER(?) OR LOWER(DESCRIPTION) LIKE LOWER(?) AND AVAILABLE=TRUE";
        searchText = "%" + searchText + "%";
        return jdbcTemplate.query(sqlQuery, this::mapRowToItem, searchText, searchText);
    }

    @Override
    public Item add(Item item) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO ITEMS (NAME, DESCRIPTION, AVAILABLE,OWNER,REQUEST) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(sqlQuery, new String[]{"id"});
                    ps.setString(1, item.getName());
                    ps.setString(2, item.getDescription());
                    ps.setBoolean(3, item.getAvailable());
                    ps.setLong(4, item.getOwner());
                    ps.setLong(5, 0);
                    return ps;
                },
                keyHolder);
        item.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return getItemById(item.getId());
    }

    @Override
    public Item getItemById(long itemId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE ID = ? ";
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet(sqlQuery, itemId);
        if (itemRows.next()) {
            Item item = Item.builder()
                    .id(itemRows.getLong("ID"))
                    .name(itemRows.getString("NAME"))
                    .description(itemRows.getString("DESCRIPTION"))
                    .available(itemRows.getBoolean("AVAILABLE"))
                    .owner(itemRows.getLong("OWNER"))
                    .request(itemRows.getLong("REQUEST"))
                    .build();
            log.info("Найден item с id {}", itemId);
            return item;
        }
        log.warn("Item with id {} not found", itemId);
        throw new ResourceNotFoundException("Item not found");
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        if (getItemById(itemId).getOwner() != userId) {
            throw new ResourceNotFoundException("Отсутствует user под id");
        }
        Item existingItem = getItemById(itemId);
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        String sqlQuery = "UPDATE ITEMS SET NAME = ?, DESCRIPTION = ?, AVAILABLE = ?,OWNER = ?,REQUEST=? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, existingItem.getName(), existingItem.getDescription(),
                existingItem.getAvailable(), existingItem.getOwner(), existingItem.getRequest(), itemId);
        log.info("Item with id {} successfully updated", itemId);
        return getItemById(itemId);
    }

    private Item mapRowToItem(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("ID"));
        item.setName(rs.getString("NAME"));
        item.setDescription(rs.getString("DESCRIPTION"));
        item.setAvailable(rs.getBoolean("AVAILABLE"));
        item.setOwner(rs.getLong("OWNER"));
        item.setRequest(rs.getLong("REQUEST"));
        return item;
    }
}