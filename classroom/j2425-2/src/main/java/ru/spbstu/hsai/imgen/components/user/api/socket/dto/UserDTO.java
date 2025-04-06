package ru.spbstu.hsai.imgen.components.user.api.socket.dto;

public record UserDTO (
        Integer userID,
        String login,
        Integer quota
){
}
