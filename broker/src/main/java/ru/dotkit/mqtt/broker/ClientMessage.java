package ru.dotkit.mqtt.broker;

import java.util.Date;

import ru.dotkit.mqtt.utils.messages.AbstractMessage;

/**
 * Created by ssv on 29.11.2017.
 */

final class ClientMessage<T extends AbstractMessage> {

    private final T _message;
    private final String _clientId;
    private final Date _receiveTime;

    public T getMessage() {
        return _message;
    }

    public String getClientId() {
        return _clientId;
    }

    public Date getReceiveTime() {
        return _receiveTime;
    }

    public ClientMessage(T message, String clientId, Date receiveTime){
        _message = message;
        _clientId = clientId;
        _receiveTime = receiveTime;
    }
}
