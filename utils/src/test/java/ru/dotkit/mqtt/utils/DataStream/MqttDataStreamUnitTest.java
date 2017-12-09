package ru.dotkit.mqtt.utils.DataStream;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Created by ssv on 08.12.2017.
 */
public class MqttDataStreamUnitTest {

    @Test
    public void readRemainingLength() throws Exception {

        InputStream inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x00});
        MqttDataStream ms = new MqttDataStream(inputStream, null);
        assertEquals(0, ms.readRemainingLength());

        inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x01});
        ms = new MqttDataStream(inputStream, null);
        assertEquals(0x01 * 128 + 0x00, ms.readRemainingLength());

        inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x01});
        ms = new MqttDataStream(inputStream, null);
        assertEquals(0x00 + 0x00 * 128 + 0x01 * 128 * 128, ms.readRemainingLength());

        inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x01});
        ms = new MqttDataStream(inputStream, null);
        assertEquals(0x00 * 128 + 0x00 * 128 * 128 + 0x01 * 128 * 128 * 128, ms.readRemainingLength());
    }

    @Test(expected = IOException.class)
    public void readRemainingLength_BadRemainingLength1() throws Exception {

        InputStream inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x80});
        MqttDataStream ms = new MqttDataStream(inputStream, null);
        ms.readRemainingLength();
    }

    @Test(expected = IOException.class)
    public void readRemainingLength_BadRemainingLength2() throws Exception {

        InputStream inputStream = new ByteArrayInputStream(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80});
        MqttDataStream ms = new MqttDataStream(inputStream, null);
        ms.readRemainingLength();
    }

    @Test
    public void readString() throws Exception {

        InputStream inputStream = new ByteArrayInputStream(new byte[]{0x00, 0x03, 'S', 'S', 'V'});
        MqttDataStream ms = new MqttDataStream(inputStream, null);
        long p0 = ms.getInputPosition();
        String rs = ms.readString();
        assertEquals("SSV", rs);
        assertEquals(5, ms.getInputPosition() - p0);
    }

    @Test
    public void readUShort() throws Exception {

        InputStream inputStream = new ByteArrayInputStream(new byte[]{0, 1});
        MqttDataStream ms = new MqttDataStream(inputStream, null);
        assertEquals(1, ms.readUShort());

        inputStream = new ByteArrayInputStream(new byte[]{1, 2});
        ms = new MqttDataStream(inputStream, null);
        assertEquals(258, ms.readUShort());
    }

    @Test
    public void writeRemainingLength() throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MqttDataStream ms = new MqttDataStream(null, outputStream);
        ms.writeRemainingLength(0);
        assertArrayEquals(new byte[]{0x00}, outputStream.toByteArray());

        outputStream = new ByteArrayOutputStream();
        ms = new MqttDataStream(null, outputStream);
        ms.writeRemainingLength(128);
        assertArrayEquals(new byte[]{(byte) 0x80, (byte) 0x01}, outputStream.toByteArray());
    }

    @Test
    public void writeString() throws Exception {

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        MqttDataStream ms = new MqttDataStream(null, ostream);
        String ss = "SSV-ЙЦУКЕН";
        long p0 = ms.getOutputPosition();
        ms.writeString(ss);
        int len = (int) (ms.getOutputPosition() - p0);
        assertEquals(ss.getBytes("UTF-8").length + 2, len);

        ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
        MqttDataStream ms2 = new MqttDataStream(istream, null);
        long p2 = ms2.getOutputPosition();
        String rs = ms2.readString();
        assertEquals(ss, rs);
        assertEquals(len, ms2.getInputPosition() - p2);
    }

    @Test
    public void writeUShort() throws Exception {

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        MqttDataStream ms = new MqttDataStream(null, ostream);
        ms.writeUShort(65001);

        ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
        MqttDataStream ms2 = new MqttDataStream(istream, null);
        int d = ms2.readUShort();
        assertEquals(65001, d);
    }
}