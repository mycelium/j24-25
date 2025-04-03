package ru.spbstu.telematics.java;

import java.util.Objects;

public class LiServerConfig {
    private final String host;
    private final int port;
    private final int threadNum;
    private final boolean isVirtual;

    /**
     * Конструктор с параметрами:
     * @param host           - хост, на котором будет работать сервер.
     * @param port           - порт для входящих соединений.
     * @param threadNum - количество потоков.
     * @param isVirtual      - true для виртуальных потоков.
     * @throws IllegalArgumentException - если порт или кол-во потоков некорректны.
     */
    public LiServerConfig(String host, int port, int threadNum, boolean isVirtual) {
        checkHost(host);
        checkPort(port);
        checkThreadNum(threadNum);
        this.host = host;
        this.port = port;
        this.threadNum = threadNum;
        this.isVirtual = isVirtual;
    }

    //метод для проверки корректности значения порта
    private void checkPort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Порт должен быть в диапазоне от 1 до 65535");
        }
    }

    //метод для проверки корректности значения кол-ва потоков
    private void checkThreadNum(int threadNum) {
        if (threadNum < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть не меньше, чем 1");
        }
    }

    //метод для проверки корректности значения хоста
    private void checkHost(String host) {
        Objects.requireNonNull(host, "Host не может быть null");
        if (host.isBlank()) {
            throw new IllegalArgumentException("Host не может быть пустым");
        }
    }

    //геттеры
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    //метод для создания дефолтной конфигурации сервера
    public static LiServerConfig defaultConfig() {
        return new LiServerConfig("localhost", 8080, 10, false);
    }
}









