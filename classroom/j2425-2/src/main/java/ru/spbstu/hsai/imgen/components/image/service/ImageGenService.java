package ru.spbstu.hsai.imgen.components.image.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ImageGenService {

    @Autowired
    ImageGenApi imageGenApi;
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

}
