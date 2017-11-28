package ru.dotkit.mqtt.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.ConnAckMessage;
import ru.dotkit.mqtt.utils.messages.DisconnectMessage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class DisconnectMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = (byte)(AbstractMessage.DISCONNECT << 4);
        byte[] bytes = new byte[]{fh, 0x00};

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(DisconnectMessage.class, am.getClass());
        assertEquals(AbstractMessage.DISCONNECT, am.getMessageType());

        DisconnectMessage m = (DisconnectMessage) am;
        m.decode(in, fh, p);
        assertEquals(0, m.getRemainingLength());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.encode(out, p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
