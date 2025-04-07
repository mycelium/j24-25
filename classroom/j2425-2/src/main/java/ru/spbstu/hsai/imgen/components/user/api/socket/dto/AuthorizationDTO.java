package ru.spbstu.hsai.imgen.components.user.api.socket.dto;

public record AuthorizationDTO(Integer code, String accessToken, Integer quota){}
