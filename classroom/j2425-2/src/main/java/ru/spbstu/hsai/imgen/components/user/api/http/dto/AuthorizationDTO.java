package ru.spbstu.hsai.imgen.components.user.api.http.dto;

public record AuthorizationDTO(Integer code, String accessToken, Integer quota){}
