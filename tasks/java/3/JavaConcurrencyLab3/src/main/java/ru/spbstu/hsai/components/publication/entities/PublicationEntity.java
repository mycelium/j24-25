package ru.spbstu.hsai.components.publication.entities;

public record PublicationEntity(
        Integer publicationId,
        String userUUID,
        String text
) {}