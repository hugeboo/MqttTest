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

import ru.dotkit.mqtt.utils.CodecUtils;

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
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        if (m_remainingLength != 2) throw new Exception();

        int b = stream.read();
        if ((b & 0xFE) != 0x00) throw new Exception();
        m_sessionPresent = (b & 0x01) == 0x01;

        b = stream.read();
        if (b < 0 || b > 5) throw new Exception();
        m_returnCode = (byte) b;
    }

    @Override
    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
        super.encode(stream, protocolVersion);

        CodecUtils.encodeRemainingLength(stream, 2);
        stream.write(m_sessionPresent ? 0x01 : 0x00);
        stream.write(m_returnCode);
    }
}
