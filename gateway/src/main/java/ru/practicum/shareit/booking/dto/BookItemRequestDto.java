package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private long id;

	@FutureOrPresent(groups = Create.class, message = "Время начала бронирования должно быть в будущем")
	@NotNull(groups = Create.class, message = " Время не может быть пустым")
	private LocalDateTime start;

	@Future(groups = Create.class, message = "Время окончания бронирования должно быть в будущем")
	@NotNull(groups = Create.class, message = " Время не может быть пустым")
	private LocalDateTime end;

	@NotNull(groups = Create.class, message = "Предмет не может быть пустым")
	private ItemDto item;

	private UserDto booker;

	private BookingState status;
}
