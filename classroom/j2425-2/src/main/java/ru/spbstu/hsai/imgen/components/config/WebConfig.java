package ru.spbstu.hsai.imgen.components.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.spbstu.hsai.imgen.components.config",
                                "ru.spbstu.hsai.imagen.components.image.api.http",
                                "ru.spbstu.hsai.imagen.components.user.api.http"
                                })
public class WebConfig {

}
