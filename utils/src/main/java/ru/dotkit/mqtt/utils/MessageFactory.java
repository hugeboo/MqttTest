package ru.dotkit.mqtt.utils;

import android.support.annotation.Nullable;

import ru.dotkit.mqtt.utils.messages.AbstractMessage;
import ru.dotkit.mqtt.utils.messages.ConnAckMessage;
import ru.dotkit.mqtt.utils.messages.ConnectMessage;
import ru.dotkit.mqtt.utils.messages.DisconnectMessage;
import ru.dotkit.mqtt.utils.messages.PingReqMessage;
import ru.dotkit.mqtt.utils.messages.PingRespMessage;
import ru.dotkit.mqtt.utils.messages.PubAckMessage;
import ru.dotkit.mqtt.utils.messages.PubCompMessage;
import ru.dotkit.mqtt.utils.messages.PubRecMessage;
import ru.dotkit.mqtt.utils.messages.PubRelMessage;
import ru.dotkit.mqtt.utils.messages.PublishMessage;
import ru.dotkit.mqtt.utils.messages.SubAckMessage;
import ru.dotkit.mqtt.utils.messages.SubscribeMessage;
import ru.dotkit.mqtt.utils.messages.UnsubAckMessage;
import ru.dotkit.mqtt.utils.messages.UnsubscribeMessage;

/**
 * Created by Sergey on 26.11.2017.
 */

public final class MessageFactory {

    @Nullable
    public static AbstractMessage Create(byte fixHeader, byte protocolVersion){
        switch ((fixHeader & 0xF0) >> 4) {
            case 1:
                return new ConnectMessage();
            case 2:
                return new ConnAckMessage();
            case 3:
                return new PublishMessage();
            case 4:
                return new PubAckMessage();
            case 5:
                return new PubRecMessage();
            case 6:
                return new PubRelMessage();
            case 7:
                return new PubCompMessage();
            case 8:
                return new SubscribeMessage();
            case 9:
                return new SubAckMessage();
            case 10:
                return new UnsubscribeMessage();
            case 11:
                return new UnsubAckMessage();
            case 12:
                return new PingReqMessage();
            case 13:
                return new PingRespMessage();
            case 14:
                return new DisconnectMessage();
            default:
                return null;
        }
    }
}
