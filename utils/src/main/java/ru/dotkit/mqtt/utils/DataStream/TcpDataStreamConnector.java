package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by ssv on 07.12.2017.
 */

public class TcpDataStreamConnector implements IDataStreamConnector {

    private Socket _socket;
    private IDataStream _stream;

    @Override
    public final void connect(String connectionString)
            throws IOException, IllegalArgumentException, NullPointerException {

        if (isConnected()) throw new IOException("Already connected");
        if (connectionString == null) throw new NullPointerException();

        String[] splits = connectionString.split(":");
        if (splits.length != 2) throw new IllegalArgumentException();

        int port;
        try {
            port = Integer.decode(splits[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("NumberFormatException: " + ex.getMessage());
        }

        _socket = new Socket(splits[0], port);
        _stream = createDataStream(_socket);
    }

    protected IDataStream createDataStream(Socket socket) throws IOException {
        return new DataStream(socket.getInputStream(), socket.getOutputStream());
    }

    @Override
    public final IDataStream getDataStream() throws IOException {
        if (!isConnected()) throw new IOException("Not connected");
        return _stream;
    }

    @Override
    public final boolean isConnected() {
        return _socket != null && _socket.isConnected();
    }

    @Override
    public final int getReadTimeout() throws IOException {
        if (!isConnected()) throw new IOException("Not connected");
        try {
            return _socket.getSoTimeout();
        } catch (SocketException ex) {
            throw new IOException("SocketException: " + ex.getMessage());
        }
    }

    @Override
    public final void setReadTimeout(int timeout) throws IOException {
        if (!isConnected()) throw new IOException("Not connected");
        try {
            _socket.setSoTimeout(timeout);
        } catch (SocketException ex) {
            throw new IOException("SocketException: " + ex.getMessage());
        }
    }

    @Override
    public final void close() throws IOException {
        if (_stream != null) _stream.close();
        if (_socket != null) _socket.close();
    }

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
