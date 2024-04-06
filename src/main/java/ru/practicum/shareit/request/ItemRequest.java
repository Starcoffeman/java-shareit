package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(groups = Create.class, message = "Описание не может быть пустым")
    @Column(name = "description")
    private String description;

    @NotBlank(groups = Create.class, message = "Requestor не может быть равен null")
    @Column(name = "requestor_id")
    private long requestor;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    @Column(name = "created")
    private Timestamp created;

    @OneToMany(mappedBy = "requestId")
    private List<Item> items;
}
