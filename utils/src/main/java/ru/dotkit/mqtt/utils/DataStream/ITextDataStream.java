package ru.dotkit.mqtt.utils.DataStream;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 08.12.2017.
 */

public interface ITextDataStream extends IDataStream {

    String readLine() throws IOException, TimeoutException;
    void write(String s) throws IOException;
    void writeLine(String s) throws IOException;
}
