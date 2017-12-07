package ru.dotkit.mqtt.broker;

import android.util.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.dotkit.mqtt.utils.Messages.AbstractMessage;
import ru.dotkit.mqtt.utils.Messages.PublishMessage;

/**
 * Created by ssv on 29.11.2017.
 */

final class ServerContext implements Closeable {

    private static final String TAG = "MQTT Context";
    public static final int OK = 0;
    public static final int SESSION_ALREADY_EXISTS = 1;

    private final Object _collectionSync = new Object();
    private final HashMap<String, ClientSession> _mapClientSessions; // by clientId
    private final HashMap<String, PublishMessage> _mapRetainMessages; // by topicName

    public ServerContext() {
        _mapClientSessions = new HashMap<>();
        _mapRetainMessages = new HashMap<>();
        Log.d(TAG, "Created");
    }

    public void close() {
        Log.d(TAG, "Close");
        synchronized (_collectionSync) {
            for (ClientSession cs : _mapClientSessions.values()) {
                cs.close();
            }
        }
    }

    public int registerClientSession(ClientSession session) {
        synchronized (_collectionSync) {
            if (_mapClientSessions.containsKey(session.getClientId())) {
                Log.w(TAG, "Register session: '" +
                        session.getClientId() + "' - ERROR - SESSION_ALREADY_EXISTS");
                return SESSION_ALREADY_EXISTS;
            }
            _mapClientSessions.put(session.getClientId(), session);
        }
        Log.d(TAG, "Register session: '" + session.getClientId() + "' - OK");
        return OK;
    }

    public void unregisterClientSession(ClientSession session) {
        Log.d(TAG, "Unregister session: '" + session.getClientId() + "'");
        synchronized (_collectionSync) {
            if (_mapClientSessions.containsKey(session.getClientId())) {
                _mapClientSessions.remove(session.getClientId());
            }
        }
    }

    public void processNewPublishMessage(ClientSession session, PublishMessage m) {

        List<ClientSession> destSessions = new ArrayList<>();
        synchronized (_collectionSync) {
            for (ClientSession s : _mapClientSessions.values()) {
                if (s.checkSubscribtion(m.getTopicName()) != AbstractMessage.QOS_RESERVED) {
                    destSessions.add(s);
                }
            }
        }
        if (m.isRetainFlag()) {
            synchronized (_collectionSync) {
                _mapRetainMessages.put(m.getTopicName(), m);
            }
        }
        for (ClientSession s : destSessions) {
            try {
                Log.d(TAG, "Publish message (topic='" +
                        m.getTopicName() + "') to client '" + s.getClientId() + "'");
                s.sendMessage(m);
            } catch (Exception ex) {
                Log.e(TAG, "Send message exception", ex);
            }
        }
    }

    public void processNewSubscriptions(ClientSession session, List<ClientSubscription> subs) {

        List<PublishMessage> retainMessages = new ArrayList<>();
        synchronized (_collectionSync) {
            for (ClientSubscription cs : subs) {
                for (PublishMessage cm : _mapRetainMessages.values()) {
                    if (cs.getTopicFilter().match(cm.getTopicName())) {
                        retainMessages.add(cm);
                    }
                }
            }
        }
        for (PublishMessage m : retainMessages) {
            try {
                Log.d(TAG, "Retain message (topic='" +
                        m.getTopicName() + "') to client '" + session.getClientId() + "'");
                session.sendMessage(m);
            } catch (Exception ex) {
                Log.e(TAG, "Send retain message exception", ex);
            }
        }
    }
}
