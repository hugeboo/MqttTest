package ru.dotkit.mqtt.utils.DataStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * Коннектор потока ввода/вывода.
 * Управляет подключением/отключением и параметрами передачи данных
 */

public interface IDataStreamConnector extends Closeable {

    /**
     * Подключиться к источнику данных
     *
     * @param connectionString параметры подключения.
     *                         Строка зависит от реализации коннектора,
     *                         может иметь вид: <par1>=<val1>;<par2>=<val2>;...
     * @throws IOException ошибка подключения
     */
    void connect(String connectionString) throws IOException;

    /**
     * Получить поток ввода/вывода
     *
     * @return поток ввода/вывода
     * @throws IOException нет подключения к источнику данных
     */
    IDataStream getDataStream() throws IOException;

    /**
     * Статус подключения
     *
     * @return коннектор подключен
     */
    boolean isConnected();

    /**
     * Таймаут чтения данных
     *
     * @return таймаут в мсек, 0 - бесконечное ожидание данных
     * @throws IOException нет подключения к источнику данных
     */
    int getReadTimeout() throws IOException;

    /**
     * Таймаут чтения данных
     *
     * @param timeout таймаут в мсек, 0 - бесконечное ожидание данных
     * @throws IOException нет подключения к источнику данных
     */
    void setReadTimeout(int timeout) throws IOException;
}
