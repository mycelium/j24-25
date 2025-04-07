package ru.spbstu.hsai.imgen.components.user.api.socket.dto;

import java.util.List;

public record UserListDTO(
        Integer code,
        List<UserDTO> users
) {
}
