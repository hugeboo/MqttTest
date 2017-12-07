package ru.dotkit.mqtt.utils.DataStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ssv on 07.12.2017.
 */

public final class MemoryMqttDataStream extends MemoryDataStream {

    public MemoryMqttDataStream(byte[] inputData) throws IOException {
        super(inputData);
    }
}
