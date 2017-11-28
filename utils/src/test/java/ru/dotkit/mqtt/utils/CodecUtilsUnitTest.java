package ru.dotkit.mqtt.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Created by ssv on 28.11.2017.
 */
public class CodecUtilsUnitTest {

    @Test
    public void decodeRemainingLength() throws Exception {

        InputStream stream = new ByteArrayInputStream(new byte[0]);
        assertEquals(-1, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x00});
        assertEquals(0, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x01});
        assertEquals(0x01 * 128 + 0x00, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x01});
        assertEquals(0x00 + 0x00 * 128 + 0x01 * 128 * 128, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x01});
        assertEquals(0x00 + 0x00 * 128 + 0x00 * 128 * 128 + 0x01 * 128 * 128 * 128, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x80});
        assertEquals(-1, CodecUtils.decodeRemainingLength(stream));

        stream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80});
        assertEquals(-1, CodecUtils.decodeRemainingLength(stream));
    }

    @Test(expected = NullPointerException.class)
    public void decodeRemainingLength_Null() throws Exception {
        CodecUtils.decodeRemainingLength(null);
    }

    @Test
    public void encodeRemainingLength() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CodecUtils.encodeRemainingLength(stream, 0);
        assertArrayEquals(new byte[]{0x00}, stream.toByteArray());

        stream = new ByteArrayOutputStream();
        CodecUtils.encodeRemainingLength(stream, 128);
        assertArrayEquals(new byte[]{(byte)0x80, (byte)0x01}, stream.toByteArray());
    }

    @Test(expected = NullPointerException.class)
    public void encodeRemainingLength_Null() throws Exception {
        CodecUtils.encodeRemainingLength(null, 0);
    }

    @Test
    public void readUShort() throws Exception {
    }

    @Test
    public void readString() throws Exception {
    }

    @Test
    public void writeUShort() throws Exception {
    }

    @Test
    public void writeString() throws Exception {
    }

}