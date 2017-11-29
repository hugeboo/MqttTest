package ru.dotkit.mqtt.client;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Sergey on 25.11.2017.
 */

public final class Example_Client {

    private static final String LOG_TAG = "myServerApp";

    // ip адрес сервера, который принимает соединения
    private String mServerName = "localhost";

    // номер порта, на который сервер принимает соединения
    private int mServerPort = 6789;

    // сокет, через которий приложения общается с сервером
    private Socket mSocket = null;

    public Example_Client() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }

    /**
     * Открытие нового соединения. Если сокет уже открыт, то он закрывается.
     */
    public void openConnection() throws IOException {

        /* Освобождаем ресурсы */
        closeConnection();
        /* Создаем новый сокет. Указываем на каком компютере и порту запущен наш процесс, который будет принамать наше соединение. */
        mSocket = new Socket(mServerName, mServerPort);
    }

    /**
     * Метод для закрытия сокета, по которому мы общались.
     */
    public void closeConnection() throws IOException {

        /* Проверяем сокет. Если он не зарыт, то закрываем его и освобдождаем соединение.*/
        if (mSocket != null) {
            try {
                if (!mSocket.isClosed()) {
                    mSocket.close();
                }
            } finally {
                mSocket = null;
            }
        }
    }

    /**
     * Метод для отправки данных по сокету.
     *
     * @param data Данные, которые будут отправлены
     */
    public void sendData(byte[] data) throws Exception {

        /* Проверяем сокет. Если он не создан или закрыт, то выдаем исключение */
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Невозможно отправить данные. Сокет не создан или закрыт");
        }
        mSocket.getOutputStream().write(data);
        mSocket.getOutputStream().flush();
    }
}