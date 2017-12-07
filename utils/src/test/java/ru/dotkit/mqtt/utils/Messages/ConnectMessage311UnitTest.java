package ru.dotkit.mqtt.utils.Messages;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.MessageFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ssv on 28.11.2017.
 */

public class ConnectMessage311UnitTest {

    @Test
    public void createAndDecodeEncode_isCorrect() throws Exception {
        byte p = CodecUtils.VERSION_3_1_1;

        byte fh = AbstractMessage.CONNECT << 4;
        byte[] bytes = new byte[]{fh, 0x1A,
                0x00, 0x04, 'M', 'Q', 'T', 'T', //protocolName
                0x04, //protocolVersion
                (byte) 0xF6, //flags, All, qos=2
                0x00, 0x0A, //keep alive
                0x00, 0x02, 'Q', 'Q', //clientId
                0x00, 0x01, 'A', //willTopic
                0x00, 0x01, 'B', //willMessage
                0x00, 0x01, 'C', //userName
                0x00, 0x01, 'D' //password
        };

        InputStream in = new ByteArrayInputStream(bytes, 1, bytes.length - 1);
        AbstractMessage am = MessageFactory.Create(fh, p);
        assertEquals(ConnectMessage.class, am.getClass());
        assertEquals(AbstractMessage.CONNECT, am.getMessageType());

        ConnectMessage m = (ConnectMessage) am;
        m.decode(in, fh, p);
        assertEquals("MQTT", m.getProtocolName());
        assertEquals(p, m.getProtocolVersion());
        assertTrue(m.isUserFlag());
        assertTrue(m.isPasswordFlag());
        assertTrue(m.isWillRetain());
        assertEquals(AbstractMessage.QOS_2, m.getWillQos());
        assertTrue(m.isWillFlag());
        assertTrue(m.isCleanSession());
        assertEquals(0x0A, m.getKeepAlive());
        assertEquals("QQ", m.getClientID());
        assertEquals("A", m.getWillTopic());
        assertEquals("B", m.getWillMessage());
        assertEquals("C", m.getUsername());
        assertEquals("D", m.getPassword());

        // Not implemented
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        m.encode(out, p);
//        byte[] bb = out.toByteArray();
//        assertArrayEquals(bytes, bb);
    }
}
