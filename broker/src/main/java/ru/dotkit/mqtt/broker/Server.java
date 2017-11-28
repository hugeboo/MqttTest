package ru.dotkit.mqtt.broker;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Sergey on 25.11.2017.
 */

public final class Server implements Runnable {

    /**
     * Реализация шаблона Singleton
     * {@link 'http://en.wikipedia.org/wiki/Singleton_pattern'}
     */
    private static volatile Server instance = null;

    /* Порт, на который сервер принимает соеденения */
    private final int SERVER_PORT = 6789;

    /* Сокет, который обрабатывает соединения на сервере */
    private ServerSocket serverSoket = null;

    private Server() {
    }

    public static Server getServer() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server();
                }
            }
        }
        return instance;
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            /* Создаем серверный сокет, которые принимает соединения */
            serverSoket = new ServerSocket(SERVER_PORT);
            System.out.println("Start server on port: " + SERVER_PORT);

            /* старт приема соединений на сервер */
            while (true) {

                ConnectionWorker worker = null;

                try {
                    /* ждем нового соединения */
                    worker = new ConnectionWorker(serverSoket.accept());
                    System.out.println("Get client connection");

                    /* создается новый поток, в котором обрабатывается соединение */
                    Thread t = new Thread(worker);
                    t.start();

                } catch (Exception e) {
                    System.out.println("Connection error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Cant start server on port " + SERVER_PORT + ":" + e.getMessage());
        } finally {
            /* Закрываем соединение */
            if (serverSoket != null) {
                try {
                    serverSoket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
