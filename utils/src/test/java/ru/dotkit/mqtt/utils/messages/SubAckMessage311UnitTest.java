package ru.dotkit.mqtt.utils.messages;

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

public class SubAckMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = (byte) (AbstractMessage.SUBACK << 4);
        byte[] bytes = new byte[]{fh, 0x05,
                0x03, 0x0F, //messageId
                0x00, 0x01, 0x02 //qos (payload)
        };

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(SubAckMessage.class, am.getClass());
        assertEquals(AbstractMessage.SUBACK, am.getMessageType());

        SubAckMessage m = (SubAckMessage) am;
        m.decode(in, fh, p);
        assertEquals(0x030F, m.getMessageID());
        assertArrayEquals(
                new int[]{0x00, 0x01, 0x02},
                new int[]{m.types().get(0).ordinal(), m.types().get(1).ordinal(), m.types().get(2).ordinal()});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.encode(out, p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}