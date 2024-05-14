package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    @Column(name = "description")
    private String description;

    @NotNull(groups = Create.class, message = "Requestor не может быть равен null")
    @Column(name = "requestor_id")
    private long requestor;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    @Column(name = "created")
    private Timestamp created;

    @OneToMany(mappedBy = "requestId")
    private List<Item> items;
}
