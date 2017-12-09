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
 * The attributes Qos, Dup and Retain aren't used.
 * 
 * @author andrea
 */
public class ConnAckMessage extends AbstractMessage {
    public static final byte CONNECTION_ACCEPTED = 0x00;
    public static final byte UNNACEPTABLE_PROTOCOL_VERSION = 0x01;
    public static final byte IDENTIFIER_REJECTED = 0x02;
    public static final byte SERVER_UNAVAILABLE = 0x03;
    public static final byte BAD_USERNAME_OR_PASSWORD = 0x04;
    public static final byte NOT_AUTHORIZED = 0x05;

    private byte m_returnCode;
    private boolean m_sessionPresent;

    public ConnAckMessage() {
        m_messageType = AbstractMessage.CONNACK;
    }

    public byte getReturnCode() {
        return m_returnCode;
    }

    public void setReturnCode(byte returnCode) {
        this.m_returnCode = returnCode;
    }

    public boolean isSessionPresent() {
        return this.m_sessionPresent;
    }

    public void setSessionPresent(boolean present) {
        this.m_sessionPresent = present;
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        if (m_remainingLength != 2) throw new IOException("RemainingLength must be 2");

        int b = stream.read();
        if ((b & 0xFE) != 0x00) throw new IOException("QoS must be 0");
        m_sessionPresent = (b & 0x01) == 0x01;

        b = stream.read();
        if (b < 0 || b > 5) throw new IOException("ReturnCode must be 0...5");
        m_returnCode = (byte) b;
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        stream.writeRemainingLength(2);
        stream.write(m_sessionPresent ? (byte) 0x01 : (byte) 0x00);
        stream.write(m_returnCode);
    }
}
