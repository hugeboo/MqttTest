package ru.dotkit.mqtt.broker;

/**
 * Created by Sergey on 02.12.2017.
 */

public final class ServerOptions implements Cloneable {

    private int _port = 1883;
    private int _connectionMessageTimeoutSec = 5;

    public int getPort() {
        return _port;
    }

    public void setPort(int _port) {
        this._port = _port;
    }

    public int getConnectionMessageTimeoutSec() {
        return _connectionMessageTimeoutSec;
    }

    public void setConnectionMessageTimeoutSec(int _connectionMessageTimeoutSec) {
        this._connectionMessageTimeoutSec = _connectionMessageTimeoutSec;
    }
}
