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
package ru.dotkit.mqtt.utils.messages;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.ReadedString;

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
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        if (protocolVersion == CodecUtils.VERSION_3_1_1) {
            if (m_qos == QOS_0 && m_dupFlag) {
                //bad protocol, if QoS=0 => DUP = 0
                throw new Exception("Received a PUBLISH with QoS=0 & DUP = 1, MQTT 3.1.1 violation");
            }
        }

        int varHeaderLength = 0;

        //Topic name
        ReadedString topic = CodecUtils.readString(stream);
        if (topic == null) {
            throw new Exception();
        }
        if (topic.s.contains("+") || topic.s.contains("#")) {
            throw new Exception("Received a PUBLISH with topic containting wild card chars, topic: " + topic);
        }
        m_topicName = topic.s;
        varHeaderLength += topic.byteLength;

        if (m_qos == QOS_1 || m_qos == QOS_2) {
            m_messageID = CodecUtils.readUShort(stream);
            varHeaderLength += 2;
        }

        //read the payload
        int payloadSize = m_remainingLength - varHeaderLength;
        m_payload = new byte[payloadSize];
        stream.read(m_payload);
    }

    @Override
    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
        super.encode(stream, protocolVersion);

        if (m_qos == QOS_RESERVED) {
            throw new IllegalArgumentException("Found a message with RESERVED Qos");
        }
        if (m_topicName == null || m_topicName == "") {
            throw new IllegalArgumentException("Found a message with empty or null topic name");
        }

        ByteArrayOutputStream variableHeaderBuff = new ByteArrayOutputStream();

        CodecUtils.writeString(variableHeaderBuff, m_topicName);

        if (m_qos == QOS_1 || m_qos == QOS_1) {
            if (getMessageID() == 0) {
                throw new IllegalArgumentException("Found a message with QOS 1 or 2 and not MessageID setted");
            }
            CodecUtils.writeUShort(variableHeaderBuff, getMessageID());
        }
        variableHeaderBuff.write(m_payload);

        CodecUtils.encodeRemainingLength(stream, variableHeaderBuff.size());
        stream.write(variableHeaderBuff.toByteArray());
    }
}
