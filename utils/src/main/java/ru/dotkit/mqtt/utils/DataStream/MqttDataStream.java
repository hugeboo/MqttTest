package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 07.12.2017.
 */

public final class MqttDataStream extends BaseMqttDataStream {

    public MqttDataStream(Socket socket) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
    }
}
