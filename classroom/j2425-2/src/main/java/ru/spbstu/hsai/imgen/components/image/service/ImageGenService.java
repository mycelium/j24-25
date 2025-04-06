package ru.spbstu.hsai.imgen.components.image.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.image.api.external.JanusAiSDK;
import ru.spbstu.hsai.imgen.components.image.api.socket.ImageGenController;
import ru.spbstu.server.HttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class ImageGenService {

    ImageGenApi imageGenApi = JanusAiSDK.getInstance();
    private static Logger logger = LoggerFactory.getLogger(ImageGenService.class);


    public Optional<Path> generateImage(String prompt) {
        Optional<String> optUrl = imageGenApi.generateImage(prompt + ";realistic,4k,gotic style");
        if (optUrl.isEmpty()){
            return Optional.empty();
        }

        Path imagePath = Path.of("images", String.valueOf(System.currentTimeMillis()));
        try {
            URL url = new URL(optUrl.get());
            logger.info(url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            Files.copy(connection.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Error while generating image", e);
            return Optional.empty();
        }

        return Optional.of(imagePath);
    }

    private ImageGenService(){

    }

    private static volatile ImageGenService instance;
    private static final Object monitor = new Object();

    public static ImageGenService getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new ImageGenService();
                }
            }
        }
        return instance;
    }

}
