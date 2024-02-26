package ru.practicum.shareit.item.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;


}
