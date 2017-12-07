package ru.dotkit.mqtt.utils.DataStream;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Поток ввода/вывода.
 * Поддерживает синхронные блокирующие операции ввода вывода
 */

public interface IDataStream extends Closeable {

    /**
     * Прочитать байт данных
     *
     * @return полученный байт
     * @throws IOException ошибка ввода-вывода
     * @throws TimeoutException превышено время чтения байта данных
     */
    byte read() throws IOException, TimeoutException;

    /**
     * Прочитать N байт в буфер, где N - размер буфера
     *
     * @param buffer выходной буфер
     * @throws IOException ошибка ввода-вывода
     * @throws NullPointerException выходной буфер NULL
     * @throws TimeoutException превышено время чтения N байтов данных
     */
    void read(byte[] buffer) throws IOException, NullPointerException, TimeoutException;

    /**
     * Записать байт данных
     *
     * @param b байт данных
     * @throws IOException ошибка ввода-вывода
     */
    void write(byte b) throws IOException;

    /**
     * Записать содержимое буфера
     *
     * @param bytes
     * @throws IOException ошибка ввода-вывода
     * @throws NullPointerException входной буфер NULL
     */
    void write(byte[] bytes) throws IOException, NullPointerException;

    /**
     * Текущая позиция входных данных
     *
     * @return количество прочитанных байт
     */
    long getInputPosition();

    /**
     * Текущая позиция выходных данных
     *
     * @return количество записанных байт
     */
    long getOutputPosition();

    /**
     * Flush выходных данных
     */
    void flush() throws IOException;
}
