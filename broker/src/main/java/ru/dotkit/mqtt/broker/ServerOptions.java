package ru.dotkit.mqtt.broker;

/**
 * Created by Sergey on 02.12.2017.
 */

public final class ServerOptions implements Cloneable {

    private int _port = 1883;

    public int getPort() {
        return _port;
    }

    public void setPort(int _port) {
        this._port = _port;
    }
}
