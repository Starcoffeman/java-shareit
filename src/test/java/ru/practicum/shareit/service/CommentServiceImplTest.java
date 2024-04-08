package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentServiceImpl;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testGetNameAuthorByCommentId() {
        // Arrange
        long commentId = 1L;
        long authorId = 2L;
        String expectedAuthorName = "John Doe";

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthorId(authorId);

        // Mocking commentRepository
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Mocking userService
        when(userService.getUserNameById(authorId)).thenReturn(expectedAuthorName);

        // Act
        String actualAuthorName = commentService.getNameAuthorByCommentId(commentId);

        // Assert
        assertEquals(expectedAuthorName, actualAuthorName);
    }

    @Test
    public void testGetNameAuthorByCommentId_CommentNotFound() {
        // Arrange
        long commentId = 1L;

        // Mocking commentRepository to return empty optional
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getNameAuthorByCommentId(commentId));
    }
}
