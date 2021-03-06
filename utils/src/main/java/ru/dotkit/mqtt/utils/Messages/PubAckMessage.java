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
import java.util.concurrent.TimeoutException;

import ru.dotkit.mqtt.utils.DataStream.IMqttDataStream;

/**
 * Placeholder for PUBACK message.
 * 
 * @author andrea
 */
public class PubAckMessage extends AbstractMessage {//}  MessageIDMessage {

    private int m_messageID; //could be null if Qos is == 0

    public int getMessageID() {
        return m_messageID;
    }

    public void setMessageID(int messageID) {
        this.m_messageID = messageID;
    }

    public PubAckMessage() {
        m_messageType = AbstractMessage.PUBACK;
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        m_messageID = stream.readUShort();
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        stream.writeRemainingLength(2);
        stream.writeUShort(getMessageID());
    }
}
