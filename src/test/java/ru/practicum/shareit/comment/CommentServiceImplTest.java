//package ru.practicum.shareit.comment;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practicum.shareit.comment.dto.CommentDto;
//import ru.practicum.shareit.comment.model.Comment;
//import ru.practicum.shareit.exceptions.ResourceNotFoundException;
//import ru.practicum.shareit.user.UserService;
//import ru.practicum.shareit.user.dto.UserDto;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CommentServiceImplTest {
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private CommentServiceImpl commentService;
//
//    @Test
//    void testGetNameAuthorByCommentId_ExistingComment() {
//        // Подготовка тестовых данных
//        Long commentId = 1L;
//        Long authorId = 1L;
//        UserDto ownerDto = new UserDto();
//        ownerDto.setName("Owner Name");
//        ownerDto.setEmail("owner@example.com");
//
//        Comment comment = new Comment();
//        comment.setAuthorId(authorId);
//
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//        when(userService.getUserById(authorId)).thenReturn(ownerDto);
//
//        // Выполнение метода
//        String result = commentService.getNameAuthorByCommentId(commentId);
//
//        // Проверка результатов
//        assertEquals("Owner Name", result);
//
//        // Проверка вызовов
//        verify(commentRepository, times(1)).findById(commentId);
//        verify(userService, times(1)).getUserById(authorId);
//    }
//
//    @Test
//    void testGetNameAuthorByCommentId_NonExistingComment() {
//        // Подготовка тестовых данных
//        Long commentId = 1L;
//
//        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
//
//        // Проверка исключения, если комментарий не найден
//        assertThrows(ResourceNotFoundException.class, () -> commentService.getNameAuthorByCommentId(commentId));
//
//        // Проверка вызовов
//        verify(commentRepository, times(1)).findById(commentId);
//        verifyNoInteractions(userService);
//    }
//
//}