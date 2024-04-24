package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getNameAuthorByCommentId_CommentFound_ReturnsAuthorName() {
        // Arrange
        Long commentId = 1L;
        Long authorId = 2L;
        String authorName = "John Doe";

        Comment comment = new Comment();
        comment.setAuthorId(authorId);

        UserDto authorDto = new UserDto();
        authorDto.setId(authorId);
        authorDto.setName(authorName);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userService.getUserById(authorId)).thenReturn(authorDto);

        // Act
        String result = commentService.getNameAuthorByCommentId(commentId);

        // Assert
        assertEquals(authorName, result);
        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, times(1)).getUserById(authorId);
    }

    @Test
    void getNameAuthorByCommentId_CommentNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getNameAuthorByCommentId(commentId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verifyNoInteractions(userService);
    }

}