package ru.dotkit.mqtt.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ssv on 28.11.2017.
 */
public class ReadedStringUnitTest {

    @Test
    public void ReadedString_create() {
        ReadedString rs = new ReadedString("QQQ", 3);
        assertEquals("QQQ", rs.s);
        assertEquals(3, rs.byteLength);
    }
}