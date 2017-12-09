package ru.dotkit.mqtt.utils.Messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.DataStream.MqttDataStream;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.StaticValues;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class SubscribeMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = StaticValues.VERSION_3_1_1;

        byte fh = (byte) (AbstractMessage.SUBSCRIBE << 4 | 0x02);
        byte[] bytes = new byte[]{fh, 0x0E,
                0x03, 0x0F, //messageId
                0x00, 0x01, 'A', 0x00, //topic + qos
                0x00, 0x01, 'B', 0x01, //topic + qos
                0x00, 0x01, 'C', 0x02,  //topic + qos
        };

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(SubscribeMessage.class, am.getClass());
        assertEquals(AbstractMessage.SUBSCRIBE, am.getMessageType());

        SubscribeMessage m = (SubscribeMessage) am;
        m.read(new MqttDataStream(in,null), fh, p);
        assertEquals(0x030F, m.getMessageID());
        assertEquals("A",m.subscriptions().get(0).getTopicFilter());
        assertEquals(0x00,m.subscriptions().get(0).getQos());
        assertEquals("B",m.subscriptions().get(1).getTopicFilter());
        assertEquals(0x01,m.subscriptions().get(1).getQos());
        assertEquals("C",m.subscriptions().get(2).getTopicFilter());
        assertEquals(0x02,m.subscriptions().get(2).getQos());

        // Not implemented
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        m.encode(out, p);
//        byte[] bb = out.toByteArray();
//        assertArrayEquals(bytes, bb);
    }
}
