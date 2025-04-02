package ru.spbstu.hsai.components.publication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.components.publication.dao.PublicationDaoImpl;
import ru.spbstu.hsai.components.publication.entities.PublicationEntity;

import java.util.*;

public class PublicationService {
    private PublicationDao userDao = PublicationDaoImpl.getInstance();
    static Logger logger = LoggerFactory.getLogger(PublicationService.class);

    public Collection<PublicationEntity> getPublicationsByUserUUID(String userUUID){
        logger.info("Executing getPublicationByUserUUID with userUUID: ${}", userUUID);
        return userDao.getPublicationsByUserUUID(userUUID);
    }

    public Optional<PublicationEntity> createPublication(String userUUID, String publicationText){
        logger.info("Executing createPublication with userUUID: ${} and publicationText: ${}", userUUID, publicationText);
        return userDao.createPublication(userUUID, publicationText);
    }

    private PublicationService() {}

    private static volatile PublicationService instance;
    private static final Object monitor = new Object();

    public static PublicationService getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new PublicationService();
                }
            }
        }
        return instance;
    }
}
