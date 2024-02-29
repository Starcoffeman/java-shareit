package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;

    @NotBlank
    private String name;

    private String description;

    private Boolean available;

    @JsonIgnore
    private Long owner;

    @JsonIgnore
    private Long request;

}
