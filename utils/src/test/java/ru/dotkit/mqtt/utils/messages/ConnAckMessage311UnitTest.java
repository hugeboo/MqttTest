package ru.dotkit.mqtt.utils.messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.ConnAckMessage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ssv on 28.11.2017.
 */

public class ConnAckMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = AbstractMessage.CONNACK << 4;
        byte[] bytes = new byte[]{fh, 0x02, 0x01, ConnAckMessage.BAD_USERNAME_OR_PASSWORD};

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(ConnAckMessage.class, am.getClass());
        assertEquals(AbstractMessage.CONNACK, am.getMessageType());

        ConnAckMessage m = (ConnAckMessage) am;
        m.decode(in, fh, p);
        assertEquals(true, m.isSessionPresent());
        assertEquals(ConnAckMessage.BAD_USERNAME_OR_PASSWORD, m.getReturnCode());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.encode(out, p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
