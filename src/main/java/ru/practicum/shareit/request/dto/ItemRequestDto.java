package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private long id;

    @NotNull(groups = Create.class, message = "Описание не может быть null")
    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Create.class, message = "Requestor не может быть равен null")
    private long requestor;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    private Timestamp created;

    private List<ItemDto> items;

}
