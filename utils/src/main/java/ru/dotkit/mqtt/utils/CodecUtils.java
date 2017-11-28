package ru.dotkit.mqtt.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by Sergey on 26.11.2017.
 */

public final class CodecUtils {

    public static final byte VERSION_3_1 = 3;
    public static final byte VERSION_3_1_1 = 4;

    public static final int MAX_LENGTH_LIMIT = 268435455;

    /**
     * Decode the variable remaining length as defined in MQTT v3.1 specification
     * (section 2.1).
     *
     * @return the decoded length or -1 if error.
     */
    public static int decodeRemainingLenght(InputStream in) throws IOException {
        int multiplier = 1;
        int value = 0;
        byte digit;
        // не может быть больше 4 байт
        for (int i = 0; i < 4; i++) {
            digit = (byte) in.read();
            value += (digit & 0x7F) * multiplier;
            multiplier *= 128;
            if ((digit & 0x80) == 0) {
                return value;
            }
        }
        return -1;
    }

    /**
     * Encode the value in the format defined in specification as variable length
     * array.
     *
     * @throws IllegalArgumentException if the value is not in the specification bounds
     *                                  [0..268435455].
     */
    public static void encodeRemainingLength(OutputStream stream, int value) throws Exception {
        if (value > MAX_LENGTH_LIMIT || value < 0) {
            throw new Exception("Value should in range 0.." + MAX_LENGTH_LIMIT + " found " + value);
        }
        byte digit;
        do {
            digit = (byte) (value % 128);
            value = value / 128;
            // if there are more digits to encode, set the top bit of this digit
            if (value > 0) {
                digit = (byte) (digit | 0x80);
            }
            stream.write(digit);
        } while (value > 0);
    }

    public static int readUShort(InputStream stream) throws IOException {
        return stream.read() + 256 * stream.read();
    }

    @NonNull
    public static ReadedString readString(InputStream stream) throws IOException {
        int len = readUShort(stream);
        if (len > 0) {
            byte[] sb = new byte[len];
            stream.read(sb);
            String s = new String(sb, "UTF-8");
            return new ReadedString(s, 2 + len);
        }
        return new ReadedString("",2);
    }

    public static void writeUShort(OutputStream stream, int value) throws IOException {
        byte msb = (byte) (value / 256);
        stream.write(value - msb * 256);
        stream.write(msb);
    }

    public static int writeString(OutputStream stream, String s) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        writeUShort(stream, bytes.length);
        stream.write(bytes);
        return 2 + bytes.length;
    }
}
