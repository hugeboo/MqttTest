package ru.dotkit.mqtt.utils.messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.DisconnectMessage;
import ru.dotkit.mqtt.utils.messages.PingReqMessage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class PingRecMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = (byte)(AbstractMessage.PINGREQ << 4);
        byte[] bytes = new byte[]{fh, 0x00};

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(PingReqMessage.class, am.getClass());
        assertEquals(AbstractMessage.PINGREQ, am.getMessageType());

        PingReqMessage m = (PingReqMessage) am;
        m.decode(in, fh, p);
        assertEquals(0, m.getRemainingLength());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.encode(out, p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
