package ru.dotkit.mqtt.utils.Messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.DataStream.MqttDataStream;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.StaticValues;

import static org.junit.Assert.assertEquals;

/**
 * Created by ssv on 28.11.2017.
 */

public class UnsubscribeMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = StaticValues.VERSION_3_1_1;

        byte fh = (byte) (AbstractMessage.UNSUBSCRIBE << 4 | 0x02);
        byte[] bytes = new byte[]{fh, 0x0B,
                0x03, 0x0F, //messageId
                0x00, 0x01, 'A', //topic
                0x00, 0x01, 'B', //topic
                0x00, 0x01, 'C',  //topic
        };

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(UnsubscribeMessage.class, am.getClass());
        assertEquals(AbstractMessage.UNSUBSCRIBE, am.getMessageType());

        UnsubscribeMessage m = (UnsubscribeMessage) am;
        m.read(new MqttDataStream(in,null), fh, p);
        assertEquals(0x030F, m.getMessageID());
        assertEquals("A", m.topicFilters().get(0));
        assertEquals("B", m.topicFilters().get(1));
        assertEquals("C", m.topicFilters().get(2));

        // Not implemented
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        m.encode(out, p);
//        byte[] bb = out.toByteArray();
//        assertArrayEquals(bytes, bb);
    }
}
