package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");
        owner = userRepository.save(owner);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item = itemRepository.save(item);
    }

    @Test
    void findAllByItemId() {
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItemId(item.getId());
        comment.setAuthorId(owner.getId());
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        assertNotNull(savedComment.getId());
        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getItemId(), savedComment.getItemId());
        assertEquals(comment.getAuthorId(), savedComment.getAuthorId());
    }
}