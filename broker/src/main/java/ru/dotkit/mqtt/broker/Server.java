package ru.dotkit.mqtt.broker;

import android.util.Log;
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

    private static final String TAG = "MQTT Server";
    private final ServerOptions _options;
    private ServerSocket _serverSocket;
    private Thread _serverThread;
    private ServerContext _ctx;

    public Server(ServerOptions options) {
        _options = options;
        Log.d(TAG, "Created");
    }

    @Override
    public void close() {
        Log.d(TAG, "Close");
        if (_serverThread != null) {
            _serverThread.interrupt();
        }
        if (_serverSocket != null) {
            try {
                _serverSocket.close();
                _serverSocket = null;
            } catch (IOException ex) {
                Log.e(TAG, "Close server socket exception", ex);
            }
        }
    }

    protected void finalize() {
        Log.d(TAG, "finalize");
        close();
    }

    public void start() {
        Log.d(TAG, "Start");
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
            Log.d(TAG, "Run on port " + _options.getPort());

            while (!_serverThread.isInterrupted()) {
                try {
                    Socket socket = _serverSocket.accept();
                    Log.d(TAG, "New connection from " +
                            socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    ClientSession.StartNew(_ctx, socket, _options);
                } catch (Exception ex) {
                    Log.e(TAG, "Connection exception", ex);
                }
            }

            Log.d(TAG, "Thread done (isInterrupted=" + _serverThread.isInterrupted() + ")");

        } catch (Exception ex) {
            Log.e(TAG, "Thread exception", ex);
        } finally {
            close();
        }
    }
}
