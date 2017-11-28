//package ru.dotkit.mqtt.utils;
//
//import ru.dotkit.mqtt.utils.messages.*;
//
///**
// * Created by Sergey on 25.11.2017.
// */
//
//public final class MessageDecoder {
//
//    public enum Result {
//        OK, NeedBytes, Error
//    }
//
//    private enum State {
//        Idle, Len, Body
//    }
//
//    private byte _protocolVersion;
//    private State _state = State.Idle;
//    private AbstractMessage _message = null;
//    private byte[] _buff;
//    private int _buffCount;
//
//    public MessageDecoder(byte protocolVersion){
//        _protocolVersion = protocolVersion;
//    }
//
//    public AbstractMessage getMessage(){
//        return _message;
//    }
//
//    public Result doByte(byte b) throws Exception {
//
//        switch (_state) {
//
//            case Idle:
//                // пришел первый байт
//                _message = MessageFactory.Create(b);
//                if (_message == null) {
//                    // первый байт с ошибкой - выходим
//                    return Result.Error;
//                } else {
//                    // первый байт норм - ждем длинну
//                    _buff = new byte[4];
//                    _buffCount = 0;
//                    _state = State.Len;
//                    return Result.NeedBytes;
//                }
//
//            case Len:
//                _buff[_buffCount] = b;
//                _buffCount += 1;
//                int len = CodecUtils.decodeRemainingLength(_buff, _buffCount);
//                if (len == 0) {
//                    _message.setRemainingLength(len);
//                    _state = State.Idle;
//                    return Result.OK;
//                } else if (len > 0) {
//                    _message.setRemainingLength(len);
//                    // ждем тело сообщения
//                    _buff = new byte[_message.getRemainingLength()];
//                    _buffCount = 0;
//                    _state = State.Body;
//                    return Result.NeedBytes;
//                } else {
//                    if (_buffCount >= _buff.length) {
//                        _message = null;
//                        _state = State.Idle;
//                        return Result.Error;
//                    } else {
//                        return Result.NeedBytes;
//                    }
//                }
//
//            case Body:
//                _buff[_buffCount] = b;
//                _buffCount += 1;
//                if (_buffCount >= _message.getRemainingLength()) {
//                    // тело получено - делаем сообщение и выходим
//                    if (_message.decodeMessageBody(_buff) && _message.verify(_protocolVersion)) {
//                        _state = State.Idle;
//                        return Result.OK;
//                    } else {
//                        _message = null;
//                        _state = State.Idle;
//                        return Result.Error;
//                    }
//                } else {
//                    return Result.NeedBytes;
//                }
//
//            default:
//                throw new Exception();
//        }
//    }
//}
