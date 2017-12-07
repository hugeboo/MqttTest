package ru.dotkit.mqtt.utils;

import android.support.annotation.Nullable;

import ru.dotkit.mqtt.utils.Messages.AbstractMessage;
import ru.dotkit.mqtt.utils.Messages.ConnAckMessage;
import ru.dotkit.mqtt.utils.Messages.ConnectMessage;
import ru.dotkit.mqtt.utils.Messages.DisconnectMessage;
import ru.dotkit.mqtt.utils.Messages.PingReqMessage;
import ru.dotkit.mqtt.utils.Messages.PingRespMessage;
import ru.dotkit.mqtt.utils.Messages.PubAckMessage;
import ru.dotkit.mqtt.utils.Messages.PubCompMessage;
import ru.dotkit.mqtt.utils.Messages.PubRecMessage;
import ru.dotkit.mqtt.utils.Messages.PubRelMessage;
import ru.dotkit.mqtt.utils.Messages.PublishMessage;
import ru.dotkit.mqtt.utils.Messages.SubAckMessage;
import ru.dotkit.mqtt.utils.Messages.SubscribeMessage;
import ru.dotkit.mqtt.utils.Messages.UnsubAckMessage;
import ru.dotkit.mqtt.utils.Messages.UnsubscribeMessage;

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
