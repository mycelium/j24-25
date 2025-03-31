package ru.spbstu.hsai.imgen.components.image.api.external;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.image.api.external.dto.GenerateImageJanusAiDTO;
import ru.spbstu.hsai.imgen.components.image.service.ImageGenApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JanusAiSDK implements ImageGenApi {
    private static Gson gson = new GsonBuilder().serializeNulls().create();
    private static Logger logger = LoggerFactory.getLogger(JanusAiSDK.class);
    private final static String BASE_URL = "https://llmhacker-realtime-flux-modified-flux-schnell-for-ja-p.hf.space/run/predict";

    @Override
    public Optional<String> generateImage(String prompt) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = gson.toJson(new GenerateImageJanusAiDTO(
                        List.of(prompt,1231900767,1024,1024,true,1),
                        null,
                        3,
                        10,
                        "4hc2g6kcoli"
                )).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                HashMap<String, Object> map = gson.fromJson(response.toString(), HashMap.class);
                String imageUrl = (String) ((Map<String, Object>) ((List<Object>) map.get("data")).get(0)).get("url");
                return Optional.of(imageUrl);
            }
        }catch (IOException e){
            logger.error("Error while generating image", e);
            return Optional.empty();
        }
    }

    private JanusAiSDK(){

    }

    private static volatile ImageGenApi instance;
    private static final Object monitor = new Object();

    public static ImageGenApi getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new JanusAiSDK();
                }
            }
        }
        return instance;
    }
}
