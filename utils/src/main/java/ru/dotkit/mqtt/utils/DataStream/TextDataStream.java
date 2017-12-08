package ru.dotkit.mqtt.utils.DataStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssv on 08.12.2017.
 */

public class TextDataStream extends DataStream implements ITextDataStream {

    public final static int NEXTLINE_CRLF = 0;
    public final static int NEXTLINE_CR = 0x0D;
    public final static int NEXTLINE_LF = 0x0A;

    private String _charset = "UTF-8";
    private int _nextLineType = NEXTLINE_CRLF;

    public String getCharset() {
        return _charset;
    }

    public void setCharset(String charset) {
        _charset = charset;
    }

    public int getNextLineType() {
        return _nextLineType;
    }

    public void setNextLineType(int nextLineType) {
        _nextLineType = nextLineType;
    }

    public TextDataStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        super(inputStream, outputStream);
    }

    @Override
    public final String readLine() throws IOException, TimeoutException {

        ByteArrayOutputStream s = new ByteArrayOutputStream();
        boolean end = false;

        do {
            byte b = read();
            boolean skip = false;
            switch (_nextLineType) {
                case NEXTLINE_CR:
                    if (b == 0x0D) end = true;
                    break;
                case NEXTLINE_LF:
                    if (b == 0x0A) end = true;
                    break;
                case NEXTLINE_CRLF:
                    if (b == 0x0D) skip = true;
                    else if (b == 0x0A) end = true;
                    break;
            }
            if (!end && !skip){
                s.write(b);
            }

        } while (!end);

        return new String(s.toByteArray(), _charset);
    }

    @Override
    public final void write(String s) throws IOException {
        if (s != null && !s.isEmpty()) {
            write(s.getBytes(_charset));
        }
    }

    @Override
    public final void writeLine(String s) throws IOException {
        write(s);
        writeNextLine();
    }

    private void writeNextLine() throws IOException {
        switch (_nextLineType) {
            case NEXTLINE_CR:
                write((byte) 0x0D);
                break;
            case NEXTLINE_LF:
                write((byte) 0x0A);
                break;
            case NEXTLINE_CRLF:
                write((byte) 0x0D);
                write((byte) 0x0A);
                break;
        }
    }
}
