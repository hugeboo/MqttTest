package ru.dotkit.mqtt.utils;

/**
 * Created by Sergey on 28.11.2017.
 */

public final class ReadedString {
    public final String s;
    public final int byteLength;
    public ReadedString(String s, int byteLength) {
        this.s = s;
        this.byteLength = byteLength;
    }
}
