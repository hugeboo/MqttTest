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

import java.io.InputStream;
import java.io.OutputStream;

import ru.dotkit.mqtt.utils.CodecUtils;

/**
 * Doesn't care DUP, QOS and RETAIN flags.
 * 
 * @author andrea
 */
public class DisconnectMessage extends AbstractMessage {// ZeroLengthMessage {

    public DisconnectMessage() {
        m_messageType = AbstractMessage.DISCONNECT;
    }

    @Override
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        if (m_remainingLength != 0) throw new Exception();
    }

    @Override
    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
        super.encode(stream, protocolVersion);

        CodecUtils.encodeRemainingLength(stream, 0);
    }
}
