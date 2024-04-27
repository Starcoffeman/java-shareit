package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllByItemId() {
        // Create a user
        User owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");
        userRepository.save(owner);

        // Create an item
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        Item savedItem = itemRepository.save(item);

        // Create a comment
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItemId(savedItem.getId()); // Use the ID of the saved item
        comment.setAuthorId(owner.getId()); // Use the ID of the saved user
        comment.setCreatedAt(LocalDateTime.now());

        // Attempt to save the comment
        Comment savedComment = commentRepository.save(comment);

        // Assert that the comment was saved successfully
        assertNotNull(savedComment.getId());
        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getItemId(), savedComment.getItemId());
        assertEquals(comment.getAuthorId(), savedComment.getAuthorId());
    }
}