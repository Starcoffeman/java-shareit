package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void mapToCommentDto_SingleComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreatedAt(LocalDateTime.now());

        // Act
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);

        // Assert
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreatedAt(), commentDto.getCreated());
    }

    @Test
    void mapToCommentDto_NullComment() {
        // Arrange
        Comment comment = null;

        // Act
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);

        // Assert
        assertNull(commentDto);
    }

    @Test
    void testMapToCommentDto_MultipleComments() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Test comment 1");
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Test comment 2");
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        // Act
        List<CommentDto> commentDtos = CommentMapper.mapToCommentDto(comments);

        // Assert
        assertEquals(comments.size(), commentDtos.size());
        assertEquals(comments.get(0).getId(), commentDtos.get(0).getId());
        assertEquals(comments.get(0).getText(), commentDtos.get(0).getText());
        assertEquals(comments.get(0).getCreatedAt(), commentDtos.get(0).getCreated());
        assertEquals(comments.get(1).getId(), commentDtos.get(1).getId());
        assertEquals(comments.get(1).getText(), commentDtos.get(1).getText());
        assertEquals(comments.get(1).getCreatedAt(), commentDtos.get(1).getCreated());
    }
}