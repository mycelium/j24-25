package ru.spbstu.hsai.components.publication.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lab.server.exceptions.HttpServerException;
import ru.spbstu.hsai.components.publication.api.dto.CreatePublicationDTO;
import ru.spbstu.hsai.components.publication.api.dto.PublicationDTO;
import ru.spbstu.hsai.components.publication.api.dto.UUIDDTO;
import ru.spbstu.hsai.components.publication.entities.PublicationEntity;
import ru.spbstu.hsai.components.publication.service.PublicationService;

import java.util.List;
import java.util.Optional;

public class PublicationController {
    static Logger logger = LoggerFactory.getLogger(PublicationController.class);
    public PublicationService service = PublicationService.getInstance();


    public List<PublicationDTO> getPublicationsByUserUUID(UUIDDTO uuiddto){
        return service.getPublicationsByUserUUID(uuiddto.uuid()).stream().map(PublicationController::mapFromEntity).toList();
    }

    public PublicationDTO createPublication(CreatePublicationDTO request){
        Optional<PublicationDTO> optPublication = service.createPublication(request.userUUID(), request.text()).flatMap((entity) -> Optional.of(mapFromEntity(entity)));
        if (optPublication.isPresent()){
            return optPublication.get();
        } else {
            throw new HttpServerException(500, "Internal Server Error", "The publication was not created");
        }
    }


    private PublicationController() {}

    private static volatile PublicationController instance;
    private static final Object monitor = new Object();

    public static PublicationController getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new PublicationController();
                }
            }
        }
        return instance;
    }

    private static PublicationDTO mapFromEntity(PublicationEntity entity){
        return new PublicationDTO(
                entity.userUUID(),
                entity.publicationId(),
                entity.text()
        );
    }
}
