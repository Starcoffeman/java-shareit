package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private UserDto authorDto;

    @BeforeEach
    void setUp() {
        Long commentId = 1L;
        Long authorId = 2L;
        String authorName = "John Doe";

        comment = new Comment();
        comment.setId(commentId);
        comment.setAuthorId(authorId);

        authorDto = new UserDto();
        authorDto.setId(authorId);
        authorDto.setName(authorName);
    }

    @Test
    void getNameAuthorByCommentId_CommentFound_ReturnsAuthorName() {
        Long commentId = 1L;
        Long authorId = 2L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userService.getUserById(authorId)).thenReturn(authorDto);

        String result = commentService.getNameAuthorByCommentId(commentId);
        assertEquals(authorDto.getName(), result);
        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, times(1)).getUserById(comment.getAuthorId());
    }

    @Test
    void getNameAuthorByCommentId_CommentNotFound_ThrowsResourceNotFoundException() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getNameAuthorByCommentId(commentId);
        });
        verify(commentRepository, times(1)).findById(commentId);
        verifyNoInteractions(userService);
    }
}