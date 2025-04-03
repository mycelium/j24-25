package ru.spbstu.telematics.java;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class LiHttpServer{
    private final LiServerConfig config; //конфигурация
    private final Router router; //маршрутизатор
    private ServerSocketChannel serverChannel; //канал для принятия соединений
    private final ExecutorService executor;
    private volatile boolean isRunning; //запущен ли сервер

    /**
     * Конструктор c параметрами:
     * @param config - конфигурация сервера.
     * @param router - маршрутизатор (хранит обработчики путей).
     */
    public LiHttpServer(LiServerConfig config, Router router){
        this.config = config;
        this.router = router;
        //инициализируем потоки в зависимости от конфигурации (виртуальные/заданные)
        this.executor = config.isVirtual()
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(config.getThreadNum());
    }

    //метод для запуска сервера
    public void start() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(config.getHost(), config.getPort()));
        isRunning = true;
        System.out.println("Сервер запущен на " + config.getHost() + ":" + config.getPort());

        try {
            while (isRunning) {
                try {
                    SocketChannel clientChannel = serverChannel.accept();
                    if (!executor.isShutdown()) {
                        executor.submit(() -> handleClient(clientChannel));
                    } else {
                        clientChannel.close();
                    }
                } catch (AsynchronousCloseException e) {
                    // Нормальное завершение при закрытии сервера
                    if (isRunning) {
                        throw e;
                    }
                }
            }
        } finally {
            closeServer();
        }
    }

    //метод для закрытия сервера
    public synchronized void closeServer() {
        if (!isRunning) return;
        isRunning = false;
        try{
            if(serverChannel != null && serverChannel.isOpen()){
                serverChannel.close();
            }
        }catch (IOException e){
            System.err.println("Ошибка при закрытии сервера: " + e.getMessage());
        } finally {
            if (executor != null && !executor.isShutdown()){
                executor.shutdown();
                try{
                    if (!executor.awaitTermination(2, TimeUnit.SECONDS)){
                        executor.shutdownNow();
                    }
                }catch (InterruptedException e){
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Сервер остановлен");
    }

    //метод для обработки клиентского соединения
    public void handleClient(SocketChannel clientChannel){
        try{
            new LiRequestHandler(clientChannel, router).handle();
        }catch (IOException e){
            System.err.println("Не удалось обработать клиента: " + e.getMessage());
        }finally {
            try{
                clientChannel.close();
            }catch(IOException e){
                System.err.println("Не удалось закрыть канал клиента: " + e.getMessage());
            }
        }
    }
}

