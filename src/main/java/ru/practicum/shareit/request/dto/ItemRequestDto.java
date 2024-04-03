package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.intf.Create;


import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Builder
public class ItemRequestDto {

    private long id;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Create.class, message = "Requestor не может быть равен null")
    private long requestor;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    private Timestamp created;
}
