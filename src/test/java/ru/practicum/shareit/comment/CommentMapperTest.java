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
    void mapToCommentDto_ShouldMapFieldsCorrectly() {
        Comment comment = new Comment(1L, "Great item!", 10L, 20L, LocalDateTime.now());
        CommentMapper mapper = new CommentMapper();
        CommentDto result = mapper.mapToCommentDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreatedAt(), result.getCreated());
    }

    @Test
    void mapToCommentDto_ShouldHandleNull() {
        Comment comment = null;
        CommentMapper mapper = new CommentMapper();
        CommentDto result = mapper.mapToCommentDto(comment);

        assertNull(result);
    }

    @Test
    void mapToCommentDtoList_ShouldCorrectlyMapCommentsToCommentDtos() {
        CommentMapper mapper = new CommentMapper();
        List<Comment> comments = Arrays.asList(
                new Comment(1L, "Great item!", 10L, 20L, LocalDateTime.now()),
                new Comment(2L, "Needs improvement", 11L, 21L, LocalDateTime.now().minusDays(1))
        );

        List<CommentDto> result = mapper.mapToCommentDto(comments);

        assertEquals(2, result.size());
        assertEquals(comments.get(0).getId(), result.get(0).getId());
        assertEquals(comments.get(1).getText(), result.get(1).getText());
    }
}