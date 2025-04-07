package ru.spbstu.hsai.imgen.components.user.api.http.dto;

public record UserDTO (
        Integer userID,
        String login,
        Integer quota
){
}
