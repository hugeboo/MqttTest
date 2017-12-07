package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 07.12.2017.
 */

public interface IMqttDataStream extends IDataStream {

    int readRemainingLength() throws IOException, TimeoutException;
    String readString() throws IOException, TimeoutException;
    int readUShort() throws IOException, TimeoutException;

    void writeRemainingLength(int len) throws IOException;
    void writeString(String s)throws IOException, NullPointerException;
    void writeUShort(int v)throws IOException;
}
