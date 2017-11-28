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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.ReadedString;

/**
 *
 * @author andrea
 */
public class SubscribeMessage extends AbstractMessage {// MessageIDMessage {

    private Integer m_messageID; //could be null if Qos is == 0

    public Integer getMessageID() {
        return m_messageID;
    }

    public void setMessageID(Integer messageID) {
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
        m_qos = AbstractMessage.QOSType.LEAST_ONE;
    }

    public List<Couple> subscriptions() {
        return m_subscriptions;
    }

    public void addSubscription(Couple subscription) {
        m_subscriptions.add(subscription);
    }

    @Override
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        m_messageID = CodecUtils.readUShort(stream);

        if (m_qos != QOSType.LEAST_ONE) {
            throw new Exception();
        }
        if (subscriptions().isEmpty()) {
            throw new Exception();
        }

        int p = 0;
        while (p < m_remainingLength - 2) {
            int sz = decodeSubscription(stream, this);
            p += sz;
        }
    }

    @Override
    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
        super.encode(stream, protocolVersion);

        throw new Exception("Not implemented");
    }

//    @Override
//    public boolean decodeMessageBody(byte[] body) {
//        try {
//            ByteBuffer buffer = ByteBuffer.wrap(body);
//            setMessageID(CodecUtils.readUShort(buffer));
//
//            while (buffer.position() < body.length) {
//                decodeSubscription(buffer, this);
//            }
//
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean verify(byte protocolVersion) {
//
//        if (!super.verify(protocolVersion)) return false;
//
//        if (m_qos != QOSType.LEAST_ONE) {
//            return false;
//        }
//
//        if (subscriptions().isEmpty()) {
//            return false;
//        }
//
//        return true;
//    }

    /**
     * Populate the message with couple of Qos, topic
     */
    private static int decodeSubscription(InputStream stream, SubscribeMessage message) throws Exception {
        ReadedString rs = CodecUtils.readString(stream);
        String topic = rs.s;
        byte qosByte = (byte)stream.read();
        if ((qosByte & 0xFC) > 0) { //the first 6 bits is reserved => has to be 0
            throw new Exception();
        }
        byte qos = (byte) (qosByte & 0x03);
        //TODO check qos id 000000xx
        message.addSubscription(new SubscribeMessage.Couple(qos, topic));
        return rs.byteLength + 1;
    }
}
