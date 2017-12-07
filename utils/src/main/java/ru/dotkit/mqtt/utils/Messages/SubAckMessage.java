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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.dotkit.mqtt.utils.CodecUtils;

/**
 *
 * @author andrea
 */
public class SubAckMessage extends AbstractMessage {//} MessageIDMessage {

    private int m_messageID; //could be null if Qos is == 0

    public int getMessageID() {
        return m_messageID;
    }

    public void setMessageID(int messageID) {
        this.m_messageID = messageID;
    }

    List<Byte> m_types = new ArrayList<Byte>();
    
    public SubAckMessage() {
        m_messageType = AbstractMessage.SUBACK;
    }

    public List<Byte> types() {
        return m_types;
    }

    public void addType(Byte type) {
        m_types.add(type);
    }

    @Override
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        m_messageID = CodecUtils.readUShort(stream);

        //Qos array
        for (int i = 0; i < m_remainingLength-2; i++) {
            byte qos = (byte)stream.read();
            addType(qos);
        }
    }

    @Override
    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
        super.encode(stream, protocolVersion);

        if (types().isEmpty()) {
            throw new IllegalArgumentException("Found a suback message with empty topics");
        }
        CodecUtils.encodeRemainingLength(stream, 2 + m_types.size());
        CodecUtils.writeUShort(stream, getMessageID());
        for (Byte c : m_types) {
            stream.write(c);
        }
    }
}
