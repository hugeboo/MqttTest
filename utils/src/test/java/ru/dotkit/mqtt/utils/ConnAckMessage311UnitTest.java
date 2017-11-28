package ru.dotkit.mqtt.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.ConnAckMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ssv on 28.11.2017.
 */

public class ConnAckMessage311UnitTest {

    @Test
    public void createAndDecode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;
        byte fh = AbstractMessage.CONNACK << 4;
        byte[] bytes = new byte[]{0x02, 0x01, ConnAckMessage.BAD_USERNAME_OR_PASSWORD};
        InputStream stream = new ByteArrayInputStream(bytes);

        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(ConnAckMessage.class, am.getClass());
        assertEquals(AbstractMessage.CONNACK, am.getMessageType());

        ConnAckMessage m = (ConnAckMessage) am;
        m.decode(stream, fh, p);
        assertEquals(true, m.isSessionPresent());
        assertEquals(ConnAckMessage.BAD_USERNAME_OR_PASSWORD, m.getReturnCode());
    }
}
