package ru.dotkit.mqtt.broker;

/**
 * Created by ssv on 29.11.2017.
 */

final class ClientSubscription {

    private final String _topicFilter;
    private byte _qos;

    public String getTopicFilter() {
        return _topicFilter;
    }

    public byte getQos() {
        return _qos;
    }

    public void setQos(byte qos) {
        _qos = qos;
    }

    public ClientSubscription(String topicFilter, byte qos){
        _topicFilter = topicFilter;
        _qos = qos;
    }
}
