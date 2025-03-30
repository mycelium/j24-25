package ru.spbstu.telematics.json.jsoninteraction;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

/**
 * Interface for interacting with JSONs
 * It contains methods for writing Map of (String, Object) into JSON string and
 * reading files into JSON string
 *
 * @author Astafiev Igor (StanleyStanMarsh)
 */
public interface JsonInteractor {
    /**
     * Write Map into JSON formatted string
     * @param map Map of (String, Object) that will be written
     * @return JSON formatted string
     */
    static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (json.length() > 1) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
        }
        json.append("}");
        return json.toString();
    }

    /**
     * Reads file into string.
     *
     * @param jsonFile file to read
     * @return string that contains the content of the jsonFile
     * @throws IOException when I/O error occurs while reading file
     */
    static String jsonFileToJsonString(File jsonFile) throws IOException {
        Objects.requireNonNull(jsonFile, "The file is null");
        if (!jsonFile.exists() || !jsonFile.isFile()) {
            throw new FileNotFoundException("The file does not exist or is not a file: " + jsonFile.getAbsolutePath());
        }

        return Files.readString(jsonFile.toPath());
    }
}
