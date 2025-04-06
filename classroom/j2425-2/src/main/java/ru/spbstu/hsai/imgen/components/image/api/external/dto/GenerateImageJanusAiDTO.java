package ru.spbstu.hsai.imgen.components.image.api.external.dto;

import java.util.List;

public record GenerateImageJanusAiDTO(
        List<Object> data,
        String event_data,
        Integer fn_index,
        Integer trigger_id,
        String session_hash
) {
}
