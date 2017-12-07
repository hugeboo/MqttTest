package ru.dotkit.mqtt.utils.Messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.MessageFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class PingRespMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = (byte)(AbstractMessage.PINGRESP << 4);
        byte[] bytes = new byte[]{fh, 0x00};

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(PingRespMessage.class, am.getClass());
        assertEquals(AbstractMessage.PINGRESP, am.getMessageType());

        PingRespMessage m = (PingRespMessage) am;
        m.decode(in, fh, p);
        assertEquals(0, m.getRemainingLength());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.encode(out, p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
