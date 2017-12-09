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
 * Doesn't care DUP, QOS and RETAIN flags.
 * 
 * @author andrea
 */
public class PingReqMessage extends AbstractMessage {// ZeroLengthMessage {
    
    public PingReqMessage() {
        m_messageType = AbstractMessage.PINGREQ;
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        if (m_remainingLength != 0) throw new IOException("RemainingLength must be 0");
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        stream.writeRemainingLength(0);
    }
}
