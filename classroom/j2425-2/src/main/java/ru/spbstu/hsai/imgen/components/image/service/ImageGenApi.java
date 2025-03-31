package ru.spbstu.hsai.imgen.components.image.service;

import java.util.Optional;

public interface ImageGenApi {

    Optional<String> generateImage(String prompt);

}
