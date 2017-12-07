package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ssv on 07.12.2017.
 */

public class TcpIpDataStream extends BaseDataStream {

    public TcpIpDataStream(Socket socket) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
    }
}
