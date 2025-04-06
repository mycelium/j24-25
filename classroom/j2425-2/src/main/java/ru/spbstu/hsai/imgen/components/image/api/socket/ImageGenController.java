package ru.spbstu.hsai.imgen.components.image.api.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.image.api.socket.dto.GenerateImageDTO;
import ru.spbstu.hsai.imgen.components.image.api.socket.dto.URLDTO;
import ru.spbstu.hsai.imgen.components.image.service.ImageGenService;
import ru.spbstu.hsai.imgen.components.user.service.UserService;

import java.nio.file.Path;
import java.util.Optional;

public class ImageGenController {
    static Gson gson = new GsonBuilder().serializeNulls().create();
    static Logger logger = LoggerFactory.getLogger(ImageGenController.class);
    private ImageGenService imageGenService = ImageGenService.getInstance();
    private UserService userService = UserService.getInstance();

    public String generate(String body){
        GenerateImageDTO bodyEntity = gson.fromJson(body, GenerateImageDTO.class);
        logger.info("Executing generate method" + bodyEntity.toString());
        if (userService.getUserBySessionID(bodyEntity.accessToken()).isPresent()){
            logger.info("I'm here");
            Optional<Path> optPath = Optional.empty();
            try{
                optPath = imageGenService.generateImage(bodyEntity.text());
            }catch (Exception e){
                logger.error("Error ",e);
            }
            if (optPath.isEmpty()){
                return gson.toJson(new URLDTO(500, ""));
            }
            return gson.toJson(new URLDTO(200, optPath.get().toString()));
        }
        return gson.toJson(new URLDTO(401, ""));
    }

    private ImageGenController(){

    }

    private static volatile ImageGenController instance;
    private static final Object monitor = new Object();

    public static ImageGenController getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new ImageGenController();
                }
            }
        }
        return instance;
    }

}
