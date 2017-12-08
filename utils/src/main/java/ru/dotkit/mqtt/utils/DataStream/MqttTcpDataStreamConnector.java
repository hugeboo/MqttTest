package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ssv on 07.12.2017.
 */

public final class MqttTcpDataStreamConnector extends TcpDataStreamConnector {

    public IMqttDataStream getMqttDataStream() throws IOException {
        return (IMqttDataStream) getDataStream();
    }

    @Override
    protected IDataStream createDataStream(Socket socket) throws IOException {
        return new MqttDataStream(socket.getInputStream(), socket.getOutputStream());
    }
}
