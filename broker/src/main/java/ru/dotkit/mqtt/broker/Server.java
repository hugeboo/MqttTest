package ru.dotkit.mqtt.broker;

import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Sergey on 02.12.2017.
 */

public final class Server implements Closeable, Runnable {

    private final ServerOptions _options;
    private ServerSocket _serverSocket;
    private Thread _serverThread;
    private ServerContext _ctx;

    public Server(ServerOptions options){
        _options = options;
    }

    @Override
    public void close() {
        if (_serverThread != null) _serverThread.interrupt();
    }

    protected void finalize() {
        close();
    }

    public void start() {
        _ctx = new ServerContext();
        _serverThread = new Thread(this);
        _serverThread.start();
    }

    /**
     * Only for internal usage !!!
     */
    @Override
    public void run() {

        try {
            _serverSocket = new ServerSocket(_options.getPort());
            //System.out.println("Start server on port: " + SERVER_PORT);
            InetAddress iad = _serverSocket.getInetAddress();
            SocketAddress sa = _serverSocket.getLocalSocketAddress();

            while (true) {
                try {
                    Socket socket = _serverSocket.accept();
                    //System.out.println("Get client connection")
                    ClientSession.StartNew(_ctx, socket);
                } catch (Exception e) {
                    System.out.println("Connection error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            //System.out.println("Cant start server on port " + SERVER_PORT + ":" + e.getMessage());
        } finally {
            /* Закрываем соединение */
            if (_serverSocket != null) {
                try {
                    _serverSocket.close();
                    _serverSocket = null;
                } catch (IOException e) {
                }
            }
        }
    }
}
