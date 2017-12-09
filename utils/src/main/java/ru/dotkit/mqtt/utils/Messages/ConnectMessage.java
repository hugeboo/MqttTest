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
import ru.dotkit.mqtt.utils.StaticValues;

/**
 * The attributes Qos, Dup and Retain aren't used for Connect message
 * 
 * @author andrea
 */
public class ConnectMessage extends AbstractMessage {
    String m_protocolName;
    byte m_protocolVersion;

    //Connection flags
    boolean m_cleanSession;
    boolean m_willFlag;
    byte m_willQos;//AbstractMessage.QOSType m_willQos;
    boolean m_willRetain;
    boolean m_passwordFlag;
    boolean m_userFlag;
    int m_keepAlive;

    //Variable part
    String m_username;
    String m_password;
    String m_clientID;
    String m_willTopic;
    String m_willMessage;

    public ConnectMessage() {
        m_messageType = AbstractMessage.CONNECT;
    }

    public boolean isCleanSession() {
        return m_cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.m_cleanSession = cleanSession;
    }

    public int getKeepAlive() {
        return m_keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.m_keepAlive = keepAlive;
    }

    public boolean isPasswordFlag() {
        return m_passwordFlag;
    }

    public void setPasswordFlag(boolean passwordFlag) {
        this.m_passwordFlag = passwordFlag;
    }

    public byte getProtocolVersion() {
        return m_protocolVersion;
    }

    public void setProtocolVersion(byte procotolVersion) {
        this.m_protocolVersion = procotolVersion;
    }

    public String getProtocolName() {
        return m_protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.m_protocolName = protocolName;
    }

    public boolean isUserFlag() {
        return m_userFlag;
    }

    public void setUserFlag(boolean userFlag) {
        this.m_userFlag = userFlag;
    }

    public boolean isWillFlag() {
        return m_willFlag;
    }

    public void setWillFlag(boolean willFlag) {
        this.m_willFlag = willFlag;
    }

    public byte getWillQos() {
        return m_willQos;
    }

    public void setWillQos(byte willQos) {
        this.m_willQos = willQos;
    }

    public boolean isWillRetain() {
        return m_willRetain;
    }

    public void setWillRetain(boolean willRetain) {
        this.m_willRetain = willRetain;
    }

    public String getPassword() {
        return m_password;
    }

    public void setPassword(String password) {
        this.m_password = password;
    }

    public String getUsername() {
        return m_username;
    }

    public void setUsername(String username) {
        this.m_username = username;
    }

    public String getClientID() {
        return m_clientID;
    }

    public void setClientID(String clientID) {
        this.m_clientID = clientID;
    }

    public String getWillTopic() {
        return m_willTopic;
    }

    public void setWillTopic(String topic) {
        this.m_willTopic = topic;
    }

    public String getWillMessage() {
        return m_willMessage;
    }

    public void setWillMessage(String willMessage) {
        this.m_willMessage = willMessage;
    }

    @Override
    public void read(IMqttDataStream stream, byte fixHeader, byte protocolVersion)
            throws IOException, TimeoutException {
        super.read(stream, fixHeader, protocolVersion);

        m_protocolName = stream.readString();
        m_protocolVersion = stream.read();

        if (!(("MQIsdp".equals(m_protocolName) && m_protocolVersion == StaticValues.VERSION_3_1) ||
                ("MQTT".equals(m_protocolName) && m_protocolVersion == StaticValues.VERSION_3_1_1))) {
            throw new IOException("Bad version");
        }

        if (m_protocolVersion == StaticValues.VERSION_3_1_1) {
            if (isDupFlag() || isRetainFlag() || getQos() != QOS_0) {
                throw new IOException("Bad flags");
            }
        }

        //Connection flag
        byte connFlags = stream.read();
        if (m_protocolVersion == StaticValues.VERSION_3_1_1) {
            if ((connFlags & 0x01) != 0) { //bit(0) of connection flags is != 0
                throw new IOException("Received a CONNECT with connectionFlags[0(bit)] != 0");
            }
        }

        boolean cleanSession = ((connFlags & 0x02) >> 1) == 1 ? true : false;
        boolean willFlag = ((connFlags & 0x04) >> 2) == 1 ? true : false;
        byte willQos = (byte) ((connFlags & 0x18) >> 3);
        if (willQos > 2) {
            throw new IOException("Expected will QoS in range 0..2 but found: " + willQos);
        }
        boolean willRetain = ((connFlags & 0x20) >> 5) == 1 ? true : false;
        boolean passwordFlag = ((connFlags & 0x40) >> 6) == 1 ? true : false;
        boolean userFlag = ((connFlags & 0x80) >> 7) == 1 ? true : false;

        //a password is true iff user is true.
        if (!userFlag && passwordFlag) {
            throw new IOException("Expected password flag to true if the user flag is true but was: " + passwordFlag);
        }

        m_cleanSession = cleanSession;
        m_willFlag = willFlag;
        m_willQos = willQos;
        m_willRetain = willRetain;
        m_passwordFlag = passwordFlag;
        m_userFlag = userFlag;

        //Keep Alive timer 2 bytes
        m_keepAlive = stream.readUShort();

        if ((m_remainingLength == 12 && m_protocolVersion == StaticValues.VERSION_3_1) ||
                (m_remainingLength == 10 && m_protocolVersion == StaticValues.VERSION_3_1_1)) {
            return;
        }

        //Decode the ClientID
        m_clientID = stream.readString();
        if (m_clientID == null || m_clientID.isEmpty()) {
            throw new IOException("clientID unspecified");
        }

        //Decode willTopic
        if (willFlag) {
            m_willTopic = stream.readString();
            if (m_willTopic == null) {
                throw new IOException("willTopic unspecified");
            }
        }

        //Decode willMessage
        if (willFlag) {
            m_willMessage = stream.readString();
            if (m_willMessage == null) {
                throw new IOException("willMessage unspecified");
            }
        }

//        //Compatibility check with v3.0, remaining length has precedence over
//        //the user and password flags
//        int readed = in.readerIndex() - start;
//        if (readed == remainingLength) {
//            out.add(message);
//            return;
//        }

        //Decode username
        if (userFlag) {
            m_username = stream.readString();
            if (m_username == null) {
                throw new IOException("username unspecified");
            }
        }

//        readed = in.readerIndex() - start;
//        if (readed == remainingLength) {
//            out.add(message);
//            return;
//        }

        //Decode password
        if (passwordFlag) {
            m_password = stream.readString();
            if (m_password == null) {
                throw new IOException("password unspecified");
            }
        }
    }

    @Override
    public void write(IMqttDataStream stream, byte protocolVersion) throws IOException {
        super.write(stream, protocolVersion);

        throw new IOException("Not implemented");
    }
}
