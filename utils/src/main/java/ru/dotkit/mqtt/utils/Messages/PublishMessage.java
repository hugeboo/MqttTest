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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import ru.dotkit.mqtt.utils.DataStream.IMqttDataStream;
import ru.dotkit.mqtt.utils.DataStream.MqttDataStream;
import ru.dotkit.mqtt.utils.StaticValues;

/**
 *
 * @author andrea
 */
public class PublishMessage extends AbstractMessage {//} MessageIDMessage {

    private String m_topicName;
    private byte[] m_payload;

    private int m_messageID; //could be null if Qos is == 0

    public int getMessageID() {
        return m_messageID;
    }

    public void setMessageID(int messageID) {
        this.m_messageID = messageID;
    }

    public PublishMessage() {
        m_messageType = AbstractMessage.PUBLISH;
    }

    public String getTopicName() {
        return m_topicName;
    }

    public void setTopicName(String topicName) {
        this.m_topicName = topicName;
    }

    public byte[] getPayload() {
        return m_payload;
    }

    public void setPayload(byte[] payload) {
        this.m_payload = payload;
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        if (protocolVersion == StaticValues.VERSION_3_1_1) {
            if (m_qos == QOS_0 && m_dupFlag) {
                //bad protocol, if QoS=0 => DUP = 0
                throw new IOException("Received a PUBLISH with QoS=0 & DUP = 1, MQTT 3.1.1 violation");
            }
        }

        long p0 = stream.getInputPosition();

        //Topic name
        String topic = stream.readString();
        if (topic == null || topic.isEmpty()) {
            throw new IOException("Topic unspecified");
        }
        if (topic.contains("+") || topic.contains("#")) {
            throw new IOException("Received a PUBLISH with topic containting wild card chars, topic: " + topic);
        }
        m_topicName = topic;

        if (m_qos == QOS_1 || m_qos == QOS_2) {
            m_messageID = stream.readUShort();
        }

        //read the payload
        int varHeaderLength = (int) (stream.getInputPosition() - p0);
        int payloadSize = m_remainingLength - varHeaderLength;
        m_payload = new byte[payloadSize];
        stream.read(m_payload);
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        if (m_qos == QOS_RESERVED) {
            throw new IOException("Found a message with RESERVED Qos");
        }
        if (m_topicName == null || m_topicName.isEmpty()) {
            throw new IOException("Found a message with empty or null topic name");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IMqttDataStream varHeadStream = new MqttDataStream(null, bos);

        long p0 = varHeadStream.getOutputPosition();

        varHeadStream.writeString(m_topicName);

        if (m_qos == QOS_1 || m_qos == QOS_2) {
            if (getMessageID() == 0) {
                throw new IOException("Found a message with QOS 1 or 2 and not MessageID setted");
            }
            varHeadStream.writeUShort(getMessageID());
        }
        varHeadStream.write(m_payload);

        stream.writeRemainingLength((int) (varHeadStream.getOutputPosition() - p0));
        stream.write(bos.toByteArray());
    }
}
