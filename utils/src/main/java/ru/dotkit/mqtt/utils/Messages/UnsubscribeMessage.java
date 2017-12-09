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
public class UnsubscribeMessage extends AbstractMessage {//} MessageIDMessage {
    List<String> m_types = new ArrayList<String>();

    private int m_messageID; //could be null if Qos is == 0

    public int getMessageID() {
        return m_messageID;
    }

    public void setMessageID(int messageID) {
        this.m_messageID = messageID;
    }

    public UnsubscribeMessage() {
        m_messageType = AbstractMessage.UNSUBSCRIBE;
    }

    public List<String> topicFilters() {
        return m_types;
    }

    public void addTopicFilter(String type) {
        m_types.add(type);
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        m_messageID = stream.readUShort();

        int topicSize = m_remainingLength - 2;
        int p = 0;

        while (p < topicSize) {
            long p0 = stream.getInputPosition();
            String rs = stream.readString();
            addTopicFilter(rs);
            p += (stream.getInputPosition() - p0);
        }
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        throw new IOException("Not implemented");
    }
}
