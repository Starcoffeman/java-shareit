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

        User owner = new User();
        owner.setName("Owner Name");
        owner.setEmail("john.doe@example.com");

        long ownerId = userRepository.save(owner).getId();

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(ownerId);
        item.setRequestId(null);
        item.setComments(new ArrayList<>());

        // Выполняем метод, который тестируем
        Item savedItem = itemRepository.save(item);

        Long itemId = 1L;
        Long validAuthorId = 1L;
        // Создание комментария с корректным ID автора
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItemId(itemId);
        comment.setAuthorId(ownerId);
        comment.setCreatedAt(LocalDateTime.now());

        // Попытка сохранения комментария
        Comment savedComment = commentRepository.save(comment);

        // Проверка, что комментарий сохранен успешно
        assertNotNull(savedComment.getId());
        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getItemId(), savedComment.getItemId());
        assertEquals(comment.getAuthorId(), savedComment.getAuthorId());
    }


}