package ru.spbstu.hsai.components.publication.service;

import ru.spbstu.hsai.components.publication.entities.PublicationEntity;

import java.util.Collection;
import java.util.Optional;

public interface PublicationDao {

    public Optional<PublicationEntity> createPublication(String userUUID, String publicationText);

    public Collection<PublicationEntity> getPublicationsByUserUUID(String userUUID);

}
