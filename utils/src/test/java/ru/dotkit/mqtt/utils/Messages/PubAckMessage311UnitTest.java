package ru.dotkit.mqtt.utils.Messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.DataStream.MqttDataStream;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.StaticValues;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class PubAckMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = StaticValues.VERSION_3_1_1;

        byte fh = (byte)(AbstractMessage.PUBACK << 4);
        byte[] bytes = new byte[]{fh, 0x02, 0x0F, 0x00};

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(PubAckMessage.class, am.getClass());
        assertEquals(AbstractMessage.PUBACK, am.getMessageType());

        PubAckMessage m = (PubAckMessage) am;
        m.read(new MqttDataStream(in,null), fh, p);
        assertEquals(0x0F00, m.getMessageID());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.write(new MqttDataStream(null,out), p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
