package ru.spbstu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.server.HttpClient;
import ru.spbstu.server.HttpServer;

import java.io.IOException;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Project started");
        try {
            var server = new HttpServer(30001);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}