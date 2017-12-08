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
    public void decode(InputStream stream, byte fixHeader, byte protocolVersion) throws Exception {
        super.decode(stream, fixHeader, protocolVersion);

        m_protocolName = CodecUtils.readString(stream).s;
        m_protocolVersion = (byte) stream.read();

        if (!(("MQIsdp".equals(m_protocolName) && m_protocolVersion == StaticValues.VERSION_3_1) ||
                ("MQTT".equals(m_protocolName) && m_protocolVersion == StaticValues.VERSION_3_1_1))) {
            throw new Exception();
        }

        if (m_protocolVersion == StaticValues.VERSION_3_1_1) {
            if (isDupFlag() || isRetainFlag() || getQos() != QOS_0) {
                throw new Exception();
            }
        }

        //Connection flag
        int connFlags = stream.read();
        if (m_protocolVersion == StaticValues.VERSION_3_1_1) {
            if ((connFlags & 0x01) != 0) { //bit(0) of connection flags is != 0
                throw new Exception("Received a CONNECT with connectionFlags[0(bit)] != 0");
            }
        }

        boolean cleanSession = ((connFlags & 0x02) >> 1) == 1 ? true : false;
        boolean willFlag = ((connFlags & 0x04) >> 2) == 1 ? true : false;
        byte willQos = (byte) ((connFlags & 0x18) >> 3);
        if (willQos > 2) {
            throw new Exception("Expected will QoS in range 0..2 but found: " + willQos);
        }
        boolean willRetain = ((connFlags & 0x20) >> 5) == 1 ? true : false;
        boolean passwordFlag = ((connFlags & 0x40) >> 6) == 1 ? true : false;
        boolean userFlag = ((connFlags & 0x80) >> 7) == 1 ? true : false;

        //a password is true iff user is true.
        if (!userFlag && passwordFlag) {
            throw new Exception("Expected password flag to true if the user flag is true but was: " + passwordFlag);
        }

        m_cleanSession = cleanSession;
        m_willFlag = willFlag;
        m_willQos = willQos;
        m_willRetain = willRetain;
        m_passwordFlag = passwordFlag;
        m_userFlag = userFlag;

        //Keep Alive timer 2 bytes
        m_keepAlive = CodecUtils.readUShort(stream);

        if ((m_remainingLength == 12 && m_protocolVersion == StaticValues.VERSION_3_1) ||
                (m_remainingLength == 10 && m_protocolVersion == StaticValues.VERSION_3_1_1)) {
            return;
        }

        //Decode the ClientID
        String clientID = CodecUtils.readString(stream).s;
        if (clientID == null || clientID == "") {
            throw new Exception();
        }
        m_clientID = clientID;

        //Decode willTopic
        if (willFlag) {
            String willTopic = CodecUtils.readString(stream).s;
            if (willTopic == null) {
                throw new Exception();
            }
            m_willTopic = willTopic;
        }

        //Decode willMessage
        if (willFlag) {
            String willMessage = CodecUtils.readString(stream).s;
            if (willMessage == null) {
                throw new Exception();
            }
            m_willMessage = willMessage;
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
            String userName = CodecUtils.readString(stream).s;
            if (userName == null) {
                throw new Exception();
            }
            m_username = userName;
        }

//        readed = in.readerIndex() - start;
//        if (readed == remainingLength) {
//            out.add(message);
//            return;
//        }

        //Decode password
        if (passwordFlag) {
            String password = CodecUtils.readString(stream).s;
            if (password == null) {
                throw new Exception();
            }
            m_password = password;
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
//            m_protocolName = CodecUtils.readString(buffer);
//
//            m_protocolVersion = buffer.get();
//
//            byte connFlags = buffer.get();
//            m_cleanSession = ((connFlags & 0x02) >> 1) == 1 ? true : false;
//            m_willFlag = ((connFlags & 0x04) >> 2) == 1 ? true : false;
//            m_willQos = AbstractMessage.QOSType.values()[(byte) ((connFlags & 0x18) >> 3)];
//            m_willRetain = ((connFlags & 0x20) >> 5) == 1 ? true : false;
//            m_passwordFlag = ((connFlags & 0x40) >> 6) == 1 ? true : false;
//            m_userFlag = ((connFlags & 0x80) >> 7) == 1 ? true : false;
//
//            m_keepAlive = CodecUtils.readUShort(buffer);
//            m_clientID = CodecUtils.readString(buffer);
//
//            if (m_willFlag) {
//                m_willTopic = CodecUtils.readString(buffer);
//                m_willMessage = CodecUtils.readString(buffer);
//            }
//
//            if (m_userFlag) {
//                m_username = CodecUtils.readString(buffer);
//            }
//
//            if (m_passwordFlag) {
//                m_password = CodecUtils.readString(buffer);
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
//        if (!(("MQIsdp".equals(m_protocolName) && m_protocolVersion == CodecUtils.VERSION_3_1) ||
//                ("MQTT".equals(m_protocolName) && m_protocolVersion == CodecUtils.VERSION_3_1_1))) {
//            return false;
//        }
//
//        if (m_protocolVersion == CodecUtils.VERSION_3_1_1) {
//            //if 3.1.1, check the flags (dup, retain and qos == 0)
//            if (isDupFlag() || isRetainFlag() || getQos() != AbstractMessage.QOSType.MOST_ONE) {
//                return false;
//            }
//        }
//
//        //a password is true iff user is true.
//        if (!m_userFlag && m_passwordFlag) {
//            return false;
//        }
//
//        if (m_clientID == null ||
//                (m_willFlag && m_willTopic == null) ||
//                (m_willFlag && m_willMessage == null) ||
//                (m_userFlag && m_username == null) ||
//                (m_passwordFlag && m_password == null)) {
//            return false;
//        }
//
//        if (m_willQos == QOSType.RESERVED) {
//            return false;
//        }
//
//        return true;
//    }
}
