package ru.dotkit.mqtt.broker;

import ru.dotkit.mqtt.utils.TopicFilter;

/**
 * Created by ssv on 29.11.2017.
 */

final class ClientSubscription {

    private final TopicFilter _topicFilter;
    private byte _qos;

    public TopicFilter getTopicFilter() {
        return _topicFilter;
    }

    public byte getQos() {
        return _qos;
    }

    public void setQos(byte qos) {
        _qos = qos;
    }

    public ClientSubscription(String topicFilter, byte qos) {
        _topicFilter = new TopicFilter(topicFilter);
        _qos = qos;
    }

    public ClientSubscription(TopicFilter topicFilter, byte qos) {
        _topicFilter = topicFilter;
        _qos = qos;
    }
}
