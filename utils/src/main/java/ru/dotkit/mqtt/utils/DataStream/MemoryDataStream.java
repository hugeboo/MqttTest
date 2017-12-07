package ru.dotkit.mqtt.utils.DataStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ssv on 07.12.2017.
 */

public class MemoryDataStream extends BaseDataStream {

    public MemoryDataStream(byte[] inputData) throws IOException {
        super(new ByteArrayInputStream(inputData), new ByteArrayOutputStream());
    }

    public byte[] getOutputData() {
        return ((ByteArrayOutputStream) _outputStream).toByteArray();
    }
}
