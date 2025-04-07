package ru.spbstu.hsai.imgen.components.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.hsai.imgen.components.image.api.external.JanusAiSDK;
import ru.spbstu.hsai.imgen.components.image.service.ImageGenApi;

@Configuration
@ComponentScan("ru.spbstu.hsai.imgen.components")
public class ApplicationConfig {

    @Bean(name = "janus")
    public ImageGenApi provideImageGenApi(){
        return JanusAiSDK.getInstance();
    }

}
