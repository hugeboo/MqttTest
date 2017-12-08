package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ssv on 08.12.2017.
 */

public final class TextTcpDataStreamConnector extends TcpDataStreamConnector {

    public ITextDataStream getTextDataStream() throws IOException {
        return (ITextDataStream) getDataStream();
    }

    @Override
    protected IDataStream createDataStream(Socket socket) throws IOException {
        return new TextDataStream(socket.getInputStream(), socket.getOutputStream());
    }
}

