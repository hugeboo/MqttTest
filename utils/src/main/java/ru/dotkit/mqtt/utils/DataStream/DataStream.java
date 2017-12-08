package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 07.12.2017.
 */

public class DataStream implements IDataStream {

    protected final InputStream _inputStream;
    protected final OutputStream _outputStream;
    private long _inputPosition;
    private long _outputPosition;

    public DataStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    @Override
    public final byte read() throws IOException, TimeoutException {
        int i = _inputStream.read();
        if (i < 0) {
            throw new IOException("InputStream return -1 (end of stream)");
        }
        _inputPosition += 1;
        return (byte) i;
    }

    @Override
    public final void read(byte[] buffer) throws IOException, IllegalArgumentException, TimeoutException {
        if (buffer == null) throw new IllegalArgumentException();
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = read();
        }
    }

    @Override
    public final void write(byte b) throws IOException {
        _outputStream.write(b);
        _outputPosition += 1;
    }

    @Override
    public final void write(byte[] bytes) throws IOException, IllegalArgumentException {
        for (int i = 0; i < bytes.length; i++) {
            write(bytes[i]);
        }
    }

    @Override
    public final void close() throws IOException {
        _inputStream.close();
        _outputStream.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public final long getInputPosition() {
        return _inputPosition;
    }

    @Override
    public final long getOutputPosition() {
        return _outputPosition;
    }

    @Override
    public final void flush() throws IOException {
        _outputStream.flush();
    }
}
