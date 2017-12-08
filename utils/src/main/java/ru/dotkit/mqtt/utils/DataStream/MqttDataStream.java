package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 07.12.2017.
 */

public class MqttDataStream extends DataStream implements IMqttDataStream {

    public static final int MAX_LENGTH_LIMIT = 268435455;

    public MqttDataStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        super(inputStream, outputStream);
    }

    @Override
    public final int readRemainingLength() throws IOException, TimeoutException {
        int multiplier = 1;
        int value = 0;
        byte digit;
        // не может быть больше 4 байт
        for (int i = 0; i < 4; i++) {
            digit = read();
            value += (digit & 0x7F) * multiplier;
            multiplier *= 128;
            if ((digit & 0x80) == 0) {
                return value;
            }
        }
        throw new IOException("Bad RemainingLength");
    }

    @Override
    public final String readString() throws IOException, TimeoutException {
        int len = readUShort();
        if (len > 0) {
            byte[] sb = new byte[len];
            read(sb);
            String s = new String(sb, "UTF-8");
            return s;
        }
        return "";
    }

    @Override
    public final int readUShort() throws IOException, TimeoutException {
        return (read() & 0xFF) * 256 + (read() & 0xFF);
    }

    @Override
    public final void writeRemainingLength(int len) throws IOException {
        if (len > MAX_LENGTH_LIMIT || len < 0) {
            throw new IOException("Length should in range 0.." + MAX_LENGTH_LIMIT + ", found " + len);
        }
        byte digit;
        do {
            digit = (byte) (len % 128);
            len = len / 128;
            // if there are more digits to encode, set the top bit of this digit
            if (len > 0) {
                digit = (byte) (digit | 0x80);
            }
            write(digit);
        } while (len > 0);
    }

    @Override
    public final void writeString(String s) throws IOException, NullPointerException {
        if (s == null) throw new NullPointerException();

        byte[] bytes = s.getBytes("UTF-8");
        writeUShort(bytes.length);
        write(bytes);
    }

    @Override
    public final void writeUShort(int v) throws IOException {
        byte msb = (byte) (v / 256);
        write(msb);
        write((byte) (v - msb * 256));
    }
}
