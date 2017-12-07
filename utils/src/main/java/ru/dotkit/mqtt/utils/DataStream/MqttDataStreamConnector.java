package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ssv on 07.12.2017.
 */

public final class MqttDataStreamConnector extends TcpIpDataStreamConnector {

    @Override
    protected IDataStream CreateDataStream(Socket socket) throws IOException {
        return new MqttDataStream(socket);
    }
}
