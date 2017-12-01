package ru.dotkit.mqtt.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.dotkit.mqtt.utils.messages.PublishMessage;

/**
 * Created by ssv on 29.11.2017.
 */

final class ServerContext {

    public static final int OK = 0;
    public static final int SESSION_ALREADY_EXISTS = 1;

    private final Object _collectionSync = new Object();
    private final HashMap<String, ClientSession> _mapClientSessions;
    private final HashMap<String, ClientMessage<PublishMessage>> _mapRetainMessages;

    public ServerContext() {
        _mapClientSessions = new HashMap<>();
        _mapRetainMessages = new HashMap<>();
    }

    public int RegisterClientSession(ClientSession session) {
        synchronized (_collectionSync) {
            if (_mapClientSessions.containsKey(session.getClientId())) {
                return SESSION_ALREADY_EXISTS;
            }
            _mapClientSessions.put(session.getClientId(), session);
        }
        return OK;
    }

    public void UnregisterClientSession(ClientSession session) {
        synchronized (_collectionSync) {
            if (_mapClientSessions.containsKey(session.getClientId())) {
                _mapClientSessions.remove(session.getClientId());
            }
        }
    }

    public void ProcessNewPublishMessage(ClientSession session, PublishMessage m) {

    }

    public void ProcessNewSubscriptions(ClientSession session, List<ClientSubscription> subs) {

        List<PublishMessage> reatainMessages = new ArrayList<>();
        synchronized (_collectionSync){
            for (ClientSubscription cs: subs) {
                //cs.getTopicFilter();
                for (PublishMessage:
                     ) {
                    
                }
            }
        }

    }
}
