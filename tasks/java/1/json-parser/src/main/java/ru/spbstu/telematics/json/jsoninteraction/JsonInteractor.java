package ru.spbstu.telematics.json.jsoninteraction;

import java.io.*;
import java.util.Map;

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
     * Reads file into string
     * @param jsonFile opening file
     * @return string that contains content of the jsonFile
     * @throws IOException when I/O error occurs while reading file
     */
    static String jsonFileToJsonString(File jsonFile) throws IOException {
        if (jsonFile == null) {
            throw new NullPointerException("The file is null");
        }

        // Проверка существования файла
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("The file does not exist");
        }

        InputStream inputJsonStream = null;
        try {
            // Чтение JSON-файла
            inputJsonStream = new FileInputStream(jsonFile);
            String jsonString = new String(inputJsonStream.readAllBytes());

            return jsonString;
        } catch (IOException e) {
            throw new IOException("I/O error occurs reading from the input JSON stream", e);
        } finally {
            // Закрытие потока
            if (inputJsonStream != null) {
                try {
                    inputJsonStream.close();
                } catch (IOException e) {
                    System.err.println("I/O error occurs closing input JSON stream");
                    e.printStackTrace();
                }
            }
        }
    }
}
