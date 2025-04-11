package ru.spbstu.hsai.imgen.components.image.api.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.hsai.imgen.components.image.api.http.dto.GenerateImageDTO;
import ru.spbstu.hsai.imgen.components.image.api.http.dto.URLDTO;
import ru.spbstu.hsai.imgen.components.image.service.ImageGenService;
import ru.spbstu.hsai.imgen.components.user.service.UserService;

import java.nio.file.Path;
import java.util.Optional;

@RestController
public class ImageGenController {
    static Logger logger = LoggerFactory.getLogger(ImageGenController.class);
    @Autowired
    private ImageGenService imageGenService;
    @Autowired
    private UserService userService;

    @PostMapping("generate")
    public @ResponseBody URLDTO generate(
            @RequestBody GenerateImageDTO request
    ){
        logger.info("Executing generate method" + request);
        if (userService.getUserBySessionID(request.accessToken()).isPresent()){
            logger.info("I'm here");
            Optional<Path> optPath = Optional.empty();
            try{
                optPath = imageGenService.generateImage(request.text());
            }catch (Exception e){
                logger.error("Error ",e);
            }
            if (optPath.isEmpty()){
                return new URLDTO(500, "");
            }
            return new URLDTO(200, optPath.get().toString());
        }
        return new URLDTO(401, "");
    }
}
