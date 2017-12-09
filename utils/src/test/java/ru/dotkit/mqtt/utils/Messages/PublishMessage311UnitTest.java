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
import static org.junit.Assert.assertTrue;

/**
 * Created by ssv on 28.11.2017.
 */

public class PublishMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = StaticValues.VERSION_3_1_1;

        byte fh = (byte) (AbstractMessage.PUBLISH << 4 | 0x0B);
        byte[] bytes = new byte[]{fh, 0x08,
                0x00, 0x01, 'A', //topicName
                0x00, 0x0F, //messageId
                0x01, 0x02, 0x03}; //payload

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(PublishMessage.class, am.getClass());
        assertEquals(PublishMessage.PUBLISH, am.getMessageType());

        PublishMessage m = (PublishMessage) am;
        m.read(new MqttDataStream(in,null), fh, p);
        assertTrue(m.isDupFlag());
        assertTrue(m.isRetainFlag());
        assertEquals(AbstractMessage.QOS_1, m.getQos());
        assertEquals("A", m.getTopicName());
        assertEquals(0x000F, m.getMessageID());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, m.getPayload());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.write(new MqttDataStream(null,out), p);
        byte[] bb = out.toByteArray();
        assertArrayEquals(bytes, bb);
    }
}
