package ru.dotkit.mqtt.broker;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.dotkit.mqtt.utils.CodecUtils;
import ru.dotkit.mqtt.utils.MessageFactory;
import ru.dotkit.mqtt.utils.TopicFilter;
import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.ConnAckMessage;
import ru.dotkit.mqtt.utils.messages.ConnectMessage;
import ru.dotkit.mqtt.utils.messages.DisconnectMessage;
import ru.dotkit.mqtt.utils.messages.PingReqMessage;
import ru.dotkit.mqtt.utils.messages.PingRespMessage;
import ru.dotkit.mqtt.utils.messages.PublishMessage;
import ru.dotkit.mqtt.utils.messages.SubAckMessage;
import ru.dotkit.mqtt.utils.messages.SubscribeMessage;
import ru.dotkit.mqtt.utils.messages.UnsubscribeMessage;

/**
 * Created by ssv on 29.11.2017.
 */

final class ClientSession implements Closeable, Runnable {

    private final ServerContext _ctx;
    private final Socket _socket;
    private final HashMap<String, ClientSubscription> _mapSubscriptions = new HashMap<>();// class TopicFilter!!!
    private final Object _outputSocketSync = new Object();
    private final Object _subscriptionSync = new Object();

    private final InputStream _inputStream;
    private final OutputStream _outputStream;
    private final Thread _inputThread;

    private boolean _isConnected;
    private boolean _isClosed;

    private String _clientId;
    private byte _protocolVersion;
    private boolean _cleanSession;
    private String _username;
    private String _password;
    private int _keepAlive;

    private boolean _willFlag;
    private byte _willQos;
    private boolean _willRetain;
    private String _willTopic;
    private String _willMessage;

    public String getClientId() {
        return _clientId;
    }

    public byte getProtocolVersion() {
        return _protocolVersion;
    }

    public boolean isAlive() {
        return _inputThread.isAlive();
    }

    public boolean isConnected() {
        return _isConnected;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }

    private ClientSession(ServerContext ctx, Socket socket) throws IOException {
        _ctx = ctx;
        _socket = socket;
        _protocolVersion = CodecUtils.VERSION_3_1_1;
        //_socket.setSoTimeout();
        _inputStream = _socket.getInputStream();
        _outputStream = _socket.getOutputStream();
        _inputThread = new Thread(this);//proc);
        _inputThread.start();
    }

    public static ClientSession StartNew(ServerContext ctx, Socket socket) throws IOException {
        return new ClientSession(ctx,socket);
    }

    public void sendMessageToClient(AbstractMessage m) throws Exception {
        synchronized (_outputSocketSync) {
            m.encode(_outputStream, _protocolVersion);
        }
    }

    public byte checkSubscribtion(String topic) {
        for (ClientSubscription cs: _mapSubscriptions.values()) {
            if (cs.getTopicFilter().match(topic)){
                return cs.getQos();
            }
        }
        return AbstractMessage.QOS_RESERVED;
    }

    @Override
    public void close() {
        if (!_isClosed) {
            _isClosed = true;
            try {
                if (_inputThread != null) _inputThread.interrupt();
            } catch (Exception ex) {
            }
            try {
                if (_inputStream != null) _inputStream.close();
            } catch (Exception ex) {
            }
            try {
                if (_outputStream != null) _outputStream.close();
            } catch (Exception ex) {
            }
            try {
                if (_socket != null) _socket.close();
            } catch (Exception ex) {
            }
        }
    }

    protected void finalize() {
        close();
    }

    @Override
    public void run() {
        try {
            boolean disconnect = false;
            while (!disconnect) {
                byte fixedHeader = (byte) _inputStream.read();
                AbstractMessage m = MessageFactory.Create(fixedHeader, _protocolVersion);
                if (m != null) {
                    switch (m.getMessageType()) {
                        case AbstractMessage.CONNECT:
                            processConnect((ConnectMessage) m);
                            break;
                        case AbstractMessage.DISCONNECT:
                            processDisconnect((DisconnectMessage) m);
                            disconnect = true;
                            break;
                        case AbstractMessage.PINGREQ:
                            processPingReq((PingReqMessage) m);
                            break;
                        case AbstractMessage.PUBLISH:
                            processPublish((PublishMessage) m);
                            break;
                        case AbstractMessage.SUBSCRIBE:
                            processSubscribe((SubscribeMessage) m);
                            break;
                        case AbstractMessage.UNSUBSCRIBE:
                            processUnsubscribe((UnsubscribeMessage) m);
                            break;
                    }
                }
            }
        } catch (SocketTimeoutException ex) {
            //...
        } catch (IOException ex) {
            //...
        } catch (RuntimeException ex) {
            //...
        } catch (Exception ex) {
            //...
        } finally {
            _ctx.unregisterClientSession(this);
            //...
        }
    }

    private void processConnect(ConnectMessage m) throws Exception {
        _protocolVersion = m.getProtocolVersion();
        _cleanSession = m.isCleanSession();
        _username = m.getUsername();
        _password = m.getPassword();
        _keepAlive = m.getKeepAlive();
        _willFlag = m.isWillFlag();
        _willQos = m.getWillQos();
        _willRetain = m.isWillRetain();
        _willTopic = m.getWillTopic();
        _willMessage = m.getWillMessage();

        ConnAckMessage ack = null;

        int res = _ctx.registerClientSession(this);

        if (res == ServerContext.OK) {
            ack = new ConnAckMessage();
            ack.setReturnCode(ConnAckMessage.CONNECT);
        } else if (res == ServerContext.SESSION_ALREADY_EXISTS) {
            ack = new ConnAckMessage();
            ack.setReturnCode(ConnAckMessage.IDENTIFIER_REJECTED);
        }

        if (ack != null) {
            synchronized (_outputSocketSync) {
                ack.encode(_outputStream, _protocolVersion);
            }
        }

        if (res != ServerContext.OK) {
            throw new RuntimeException("ClientConnection rejected. Code: " + res);
        }

        _isConnected = true;
    }

    private void processDisconnect(DisconnectMessage m) {
        _isConnected = false;
    }

    private void processPingReq(PingReqMessage m) throws Exception {
        PingRespMessage ack = new PingRespMessage();

        synchronized (_outputSocketSync) {
            ack.encode(_outputStream, _protocolVersion);
        }
    }

    private void processPublish(PublishMessage m) {
        _ctx.processNewPublishMessage(this, m);
    }

    private void processSubscribe(SubscribeMessage m) throws Exception {
        SubAckMessage ack = new SubAckMessage();
        ack.setMessageID(m.getMessageID());
        List<ClientSubscription> subs = new ArrayList<>();

        synchronized (_subscriptionSync) {
            for (SubscribeMessage.Couple c : m.subscriptions()) {
                TopicFilter tf = new TopicFilter(c.getTopicFilter());
                String tfs = tf.toString();
                ClientSubscription cs;
                if (_mapSubscriptions.containsKey(tfs)) {
                    cs = _mapSubscriptions.get(tfs);
                    cs.setQos(AbstractMessage.QOS_0);//c.getQos()); поддерживаем только QOS_0 !!!
                } else {
                    cs = new ClientSubscription(tf, c.getQos());
                }
                subs.add(cs);
                ack.addType(AbstractMessage.QOS_0); // поддерживаем только QOS_0 !!!
            }
        }

        _ctx.processNewSubscriptions(this, subs);

        synchronized (_outputSocketSync) {
            ack.encode(_outputStream, _protocolVersion);
        }
    }

    private void processUnsubscribe(UnsubscribeMessage m) throws Exception {
        synchronized (_subscriptionSync) {
            for (String t : m.topicFilters()) {
                TopicFilter tf = new TopicFilter(t);
                _mapSubscriptions.remove(tf.toString());
            }
        }

        UnsubscribeMessage ack = new UnsubscribeMessage();
        ack.setMessageID(m.getMessageID());
        synchronized (_outputSocketSync) {
            ack.encode(_outputStream, _protocolVersion);
        }
    }
}
