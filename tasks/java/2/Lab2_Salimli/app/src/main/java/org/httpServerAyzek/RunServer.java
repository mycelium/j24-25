package org.httpServerAyzek;

import org.httpServerAyzek.http.HttpServer;
import org.httpServerAyzek.http.handler.HttpHandler;
import org.httpServerAyzek.http.HttpRes;
import org.httpServerAyzek.http.util.HttpReqParser;
import java.io.IOException;

public class RunServer {
    public static void main(String[] args) {
        HttpServer server = new HttpServer("localhost", 8080, 4, false);
        server.addListener("/link1", "GET", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                response.setBody("GET: It work!!!");
            }
        });
        server.addListener("/link2", "POST", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("POST: geted: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/link3", "PUT", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("PUT: geted: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/link4", "PATCH", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("PATCH: geted: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/delete", "DELETE", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                response.setBody("DELETE: done");
            }
        });
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
