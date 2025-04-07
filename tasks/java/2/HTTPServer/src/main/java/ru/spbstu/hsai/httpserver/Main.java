package ru.spbstu.hsai.httpserver;
import ru.spbstu.hsai.httpserver.common.HttpMethods;
import ru.spbstu.hsai.httpserver.common.HttpStatus;

import java.nio.file.*;
import com.google.gson.*;

public class Main {
    public static void main(String[] args) {
        try (HttpServer server = new HttpServer("localhost", 35000, 4, true)) {

            server.addHandler(HttpMethods.GET, "/files/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    String content = Files.readString(Paths.get("files/" +filename));
                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody(content);
                } catch (Exception e) {
                    response.setStatus(HttpStatus.NotFound);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody("File not found");
                }
            });

            server.addHandler(HttpMethods.POST, "/files/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    Files.writeString(Paths.get("files/" + filename), request.getBody(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                    response.setStatus(HttpStatus.Created);
                    response.setBody("File created/appended");
                    response.addHeader("Content-Type", "text/plain");
                } catch (Exception e) {
                    response.setStatus(HttpStatus.InternalServerError);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody("Error writing file");
                }
            });

            server.addHandler(HttpMethods.PUT, "/files/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    Files.writeString(Paths.get("files/" + filename), request.getBody(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody("File completely replaced");
                } catch (Exception e) {
                    response.setStatus(HttpStatus.InternalServerError);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody("Error replacing file");
                }
            });

            server.addHandler(HttpMethods.PATCH, "/files/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    Path path = Paths.get("files/" + filename);
                    String existing = Files.readString(path);
                    String updated = existing + "\n" + request.getBody();
                    Files.writeString(path, updated);
                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody("{\"status\":\"success\",\"message\":\"File %s updated\",\"lines_added\":1}");
                } catch (Exception e) {
                    response.setStatus(HttpStatus.NotFound);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody("{\"error\":\"File not found\"}");
                }
            });

            server.addHandler(HttpMethods.DELETE, "/files/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    Files.delete(Paths.get("files/" + filename));
                    response.setStatus(HttpStatus.Ok);
                    response.setBody("{\"status\":\"success\",\"message\":\"File %s deleted\"}");
                } catch (Exception e) {
                    response.setStatus(HttpStatus.NotFound);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody("{\"error\":\"" + e.getMessage() + "\"}");
                }
            });

            server.addHandler(HttpMethods.PUT, "/file-merge", (request, response) -> {
                try {
                    JsonObject json = JsonParser.parseString(request.getBody()).getAsJsonObject();
                    String sourceFile = json.get("source").getAsString();
                    String targetFile = json.get("target").getAsString();
                    boolean appendMode = json.has("mode") && json.get("mode").getAsBoolean();

                    String sourceContent = Files.readString(Paths.get("files/" + sourceFile));
                    Path path = Paths.get("files/" + targetFile);
                    if (appendMode) {
                        Files.writeString(
                                path,
                                sourceContent,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND
                        );
                    } else {
                        Files.writeString(
                                path,
                                sourceContent,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING
                        );
                    }

                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody(String.format(
                            "The contents of the %s file have been successfully added to %s (mode: %s)",
                            sourceFile, targetFile, appendMode ? "addendum" : "overwriting"
                    ));

                } catch (NoSuchFileException e) {
                    response.setStatus(HttpStatus.NotFound);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody("{\"error\":\"File not found\"}");
                } catch (Exception e) {
                    response.setStatus(HttpStatus.InternalServerError);
                    response.addHeader("Content-Type", "application/json");
                    response.setBody("{\"error\":\"Deletion failed\"}");
                }
            });
            server.addHandler(HttpMethods.GET, "/image/:filename", (request, response) -> {
                String filename = request.getPathParam("filename");
                try {
                    byte[] content = Files.readAllBytes(Paths.get("image/" +filename));
                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "image/jpeg");
                    response.setBody(content);
                } catch (Exception e) {
                    response.setStatus(HttpStatus.NotFound);
                    response.setBody("File not found");
                }
            });
            server.addHandler(HttpMethods.GET, "/user/:id", (request, response) -> {
                String id = request.getPathParam("id");
                    response.setStatus(HttpStatus.Ok);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody(id);
            });
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}