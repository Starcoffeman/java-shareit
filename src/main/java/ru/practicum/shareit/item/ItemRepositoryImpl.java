package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

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
    public List<Item> getItems() {
        String sqlQuery = "SELECT * FROM ITEMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToItem);
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
                    ps.setLong(4,item.getOwner());
                    ps.setLong(5,0);
                    return ps;
                },
                keyHolder);

        item.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return getItemById(item.getId());
    }

    @Override
    public Item getItemById(long itemId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE ID = ?";
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
    public Item update(long id, ItemDto itemDto) {
        // Retrieve the existing item from the database
        Item existingItem = getItemById(id);

        // Update the item details if provided in the request
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        // Update the item in the database
        String sqlQuery = "UPDATE ITEMS SET NAME = ?, DESCRIPTION = ?, AVAILABLE = ?,OWNER = ?,REQUEST=? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, existingItem.getName(), existingItem.getDescription(),
                existingItem.getAvailable(),existingItem.getOwner(),existingItem.getRequest(), id);

        log.info("Item with id {} successfully updated", id);

        return getItemById(id);
    }

    @Override
    public void delete(long itemId) {
        // Check if the item exists
        getItemById(itemId);

        // Delete the item from the database
        String sqlQuery = "DELETE FROM ITEMS WHERE ID = ?";
        int rowsAffected = jdbcTemplate.update(sqlQuery, itemId);

        if (rowsAffected > 0) {
            log.info("Item with id {} successfully deleted", itemId);
        } else {
            log.warn("Failed to find item with id {} for deletion", itemId);
            throw new ResourceNotFoundException("Item not found for deletion");
        }
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

    private boolean isItemAlreadyExists(String name) {
        String sqlQuery = "SELECT COUNT(*) FROM ITEMS WHERE NAME = ?";
        int count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, name);
        return count > 0;
    }

}