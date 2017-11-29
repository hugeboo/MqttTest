package ru.dotkit.mqtt.broker;

import java.util.HashMap;
import java.util.List;

import ru.dotkit.mqtt.utils.messages.PublishMessage;

/**
 * Created by ssv on 29.11.2017.
 */

final class ServerContext {

    public static final int OK = 0;
    public static final int SESSION_ALREADY_EXISTS = 1;

    private final HashMap<String, ClientSession> _mapClientSessions;
    private final HashMap<String, ClientMessage<PublishMessage>> _mapRetainMessages;

    public ServerContext() {
        _mapClientSessions = new HashMap<>();
        _mapRetainMessages = new HashMap<>();
    }

    public int RegisterClientSession(ClientSession session) {
        return OK;
    }

    public void UnregisterClientSession(ClientSession session) {

    }

    public void ProcessNewPublishMessage(ClientSession session, PublishMessage m) {

    }

    public void ProcessNewSubscriptions(ClientSession session, List<ClientSubscription> subs) {

    }
}
