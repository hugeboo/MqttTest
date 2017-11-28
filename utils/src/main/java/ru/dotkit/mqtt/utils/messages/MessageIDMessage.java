///*
// * Copyright (c) 2012-2014 The original author or authors
// * ------------------------------------------------------
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * and Apache License v2.0 which accompanies this distribution.
// *
// * The Eclipse Public License is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * The Apache License v2.0 is available at
// * http://www.opensource.org/licenses/apache2.0.php
// *
// * You may elect to redistribute this code under either of these licenses.
// */
////package org.eclipse.moquette.proto.messages;
//package ru.dotkit.mqtt.utils.messages;
//
//import android.support.annotation.CallSuper;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import ru.dotkit.mqtt.utils.CodecUtils;
//
///**
// * Base class for alla the messages that carries only MessageID. (PUBACK, PUBREC,
// * PUBREL, PUBCOMP, UNSUBACK)
// *
// * The flags dup, QOS and Retained doesn't take care.
// *
// * @author andrea
// */
//public abstract class MessageIDMessage extends AbstractMessage {
//    private Integer m_messageID; //could be null if Qos is == 0
//
//    public Integer getMessageID() {
//        return m_messageID;
//    }
//
//    public void setMessageID(Integer messageID) {
//        this.m_messageID = messageID;
//    }
//
//    @Override
//    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
//        super.decode(stream, fixHeader, protocolVersion);
//
//        //read  messageIDs
//        m_messageID = CodecUtils.readUShort(stream);
//    }
//
//    @Override
//    public void encode(OutputStream stream, byte protocolVersion) throws Exception {
//        super.encode(stream, protocolVersion);
//    }
//}
