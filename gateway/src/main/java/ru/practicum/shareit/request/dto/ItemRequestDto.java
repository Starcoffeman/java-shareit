package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Requestor не может быть равен null")
    private long requestor;

    @NotNull(message = " Время не может быть пустым")
    private Timestamp created;

    private List<ItemDto> items;
}