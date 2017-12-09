/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
//package org.eclipse.moquette.proto.messages;
package ru.dotkit.mqtt.utils.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import ru.dotkit.mqtt.utils.DataStream.IMqttDataStream;

/**
 *
 * @author andrea
 */
public class SubscribeMessage extends AbstractMessage {// MessageIDMessage {

    private int m_messageID; //could be null if Qos is == 0

    public int getMessageID() {
        return m_messageID;
    }

    public void setMessageID(int messageID) {
        this.m_messageID = messageID;
    }

    public static class Couple {

        private byte m_qos;
        private String m_topicFilter;

        public Couple(byte qos, String topic) {
            m_qos = qos;
            m_topicFilter = topic;
        }

        public byte getQos() {
            return m_qos;
        }

        public String getTopicFilter() {
            return m_topicFilter;
        }
    }

    private List<Couple> m_subscriptions = new ArrayList<Couple>();

    public SubscribeMessage() {
        //Subscribe has always QoS 1
        m_messageType = AbstractMessage.SUBSCRIBE;
        m_qos = QOS_1;
    }

    public List<Couple> subscriptions() {
        return m_subscriptions;
    }

    public void addSubscription(Couple subscription) {
        m_subscriptions.add(subscription);
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        m_messageID = stream.readUShort();

        if (m_qos != QOS_1) {
            throw new IOException("QoS is not equal QOS_1");
        }

        int p = 0;
        while (p < m_remainingLength - 2) {
            int sz = decodeSubscription(stream, this);
            p += sz;
        }

        if (subscriptions().isEmpty()) {
            throw new IOException("Subscriptions is empty");
        }
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        throw new IOException("Not implemented");
    }

    /**
     * Populate the message with couple of Qos, topic
     */
    private static int decodeSubscription(IMqttDataStream stream, SubscribeMessage message)
            throws IOException, TimeoutException {
        long p0 = stream.getInputPosition();
        String topic = stream.readString();
        byte qosByte = (byte) stream.read();
        if ((qosByte & 0xFC) > 0) { //the first 6 bits is reserved => has to be 0
            throw new IOException("QoS is equal QOS_RESERVED");
        }
        byte qos = (byte) (qosByte & 0x03);
        //TODO check qos id 000000xx
        message.addSubscription(new SubscribeMessage.Couple(qos, topic));
        return (int) (stream.getInputPosition() - p0);
    }
}
