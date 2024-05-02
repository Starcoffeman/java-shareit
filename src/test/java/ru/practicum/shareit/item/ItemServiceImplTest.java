package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void saveItem() {
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());
        
        when(userService.getUserById(1L)).thenReturn(ownerDto);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        ItemDto savedItemDto = itemService.saveItem(1L, itemDto);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(userService, times(1)).getUserById(1L);
        assertNotNull(savedItemDto.getId());
    }

    @Test
    void saveItem_negativeUserId_throwsValidationException() {
        UserDto ownerDto = new UserDto();
        ownerDto.setId(-1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());
        
        assertThrows(ValidationException.class, () -> itemService.saveItem(-1L, itemDto));
    }

    @Test
    void saveItem_nullName_throwsValidationException() {
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName(null); 
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());

        assertThrows(ValidationException.class, () -> itemService.saveItem(1L, itemDto));
    }

    @Test
    void testGetNameAuthor() {
        Item item = new Item();
        item.setId(1L);
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthorId(1L);
        comment1.setText("Comment 1");
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthorId(2L);
        comment2.setText("Comment 2");
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        item.setComments(comments);

        when(commentService.getNameAuthorByCommentId(1L)).thenReturn("Author 1");
        when(commentService.getNameAuthorByCommentId(2L)).thenReturn("Author 2");

        List<CommentDto> commentDtos = itemService.getNameAuthor(item);
        
        assertEquals(2, commentDtos.size());

        CommentDto commentDto1 = commentDtos.get(0);
        assertEquals("Comment 1", commentDto1.getText());
        assertEquals("Author 1", commentDto1.getAuthorName());
        CommentDto commentDto2 = commentDtos.get(1);
        assertEquals("Comment 2", commentDto2.getText());
        assertEquals("Author 2", commentDto2.getAuthorName());
    }

    @Test
    void testSearchItemsWithEmptySearchText() {
        String emptySearchText = "";
        List<ItemDto> result = itemService.searchItems(emptySearchText);
        
        assertEquals(0, result.size());
    }

    @Test
    void testSearchItemsWithBlankSearchText() {
        String blankSearchText = "   ";
        List<ItemDto> result = itemService.searchItems(blankSearchText);
        
        assertEquals(0, result.size());
    }

    @Test
    void testSearchItems() {
        String searchText = "test";
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("This is a test item 1");
        item1.setAvailable(true);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Another Test Item");
        item2.setDescription("This is another test item 2");
        item2.setAvailable(true);
        items.add(item2);
        
        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText))
                .thenReturn(items);
        
        List<ItemDto> result = itemService.searchItems(searchText);

        assertEquals(2, result.size());
        assertEquals("Test Item 1", result.get(0).getName());
        assertEquals("This is a test item 1", result.get(0).getDescription());
        assertEquals("Another Test Item", result.get(1).getName());
        assertEquals("This is another test item 2", result.get(1).getDescription());
    }

    @Test
    void testAddCommentEmptyText() {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "";

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void testAddCommentFutureBooking() {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";
        
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void testAddCommentTestEmpty() {
        long userId = 1L;
        long itemId = 1L;
        String commentText = " ";

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void testAddCommentItemNotFound() {
        long userId = 1L;
        long itemId = 2L;
        String commentText = "Test comment";
        
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        
        assertThrows(ResourceNotFoundException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void saveItem_nullDescription_throwsValidationException() {
        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner Name");
        ownerDto.setEmail("owner@example.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription(null); 
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setRequestId(0);
        itemDto.setComments(new ArrayList<>());
        
        assertThrows(ValidationException.class, () -> itemService.saveItem(1L, itemDto));
    }

    @Test
    void findItemsByOwner() {
        long userId = 1L;
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        
        when(itemRepository.findItemsByOwner(userId)).thenReturn(items);
        List<ItemDto> result = itemService.findItemsByOwner(userId);

        verify(itemRepository, times(1)).findItemsByOwner(userId);
        
        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void update() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(userId);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);
        
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto updatedItemDto = itemService.update(userId, itemId, itemDto);
        
        verify(itemRepository, times(1)).findById(itemId);
        
        assertEquals(itemDto.getName(), updatedItemDto.getName());
        assertEquals(itemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    void updateOwnerNotFound() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(2L);
        
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.update(userId, itemId, itemDto);
        });

        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void saveItemUserNotFound() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setRequestId(0);
        
        when(userService.getUserById(anyLong())).thenReturn(null);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.saveItem(userId, itemDto);
        });
        
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void addComment_ReturnsCommentDto_WhenValid() {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        Item item = new Item();
        item.setId(itemId);

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(itemId), eq(userId),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        CommentDto result = itemService.addComment(userId, itemId, commentText);

        assertNotNull(result);
        assertEquals(commentText, result.getText());
        assertEquals("Test User", result.getAuthorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getItemById_ItemNotFound_ThrowsResourceNotFoundException() {
        long userId = 1L;
        long itemId = 100L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(userId, itemId));
    }

    @Test
    void getItemById1() {
        long userId = 1L;
        long itemId = 1L;

        Item item = new Item();
        item.setName("banmedas");
        item.setDescription("description");
        item.setAvailable(true);
        item.setId(itemId);
        item.setOwner(userId);
        item.setComments(new ArrayList<>());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.getItemById(userId,itemId);
        assertEquals(item.getDescription(),itemDto.getDescription());
    }

    @Test
    void getItemById2() {
        long userId = 1L;
        long itemId = 1L;

        Item item = new Item();
        item.setName("banmedas");
        item.setDescription("description");
        item.setAvailable(true);
        item.setId(itemId);
        item.setOwner(userId+1);
        item.setComments(new ArrayList<>());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.getItemById(userId,itemId);
        assertEquals(item.getDescription(),itemDto.getDescription());
        assertEquals(null,itemDto.getLastBooking());
    }

}