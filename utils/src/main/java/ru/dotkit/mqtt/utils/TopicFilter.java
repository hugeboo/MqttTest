package ru.dotkit.mqtt.utils;

import java.util.StringTokenizer;

/**
 * Created by ssv on 30.11.2017.
 */

public final class TopicFilter {

    public final static String DELIM = "/";
    public final static String WILD_LEVEL = "+";
    public final static String WILD_TREE = "#";

    private final String _filter;
    private final String[] _tokens;

    public TopicFilter(String topicFilter) {
        if (topicFilter == null) throw new IllegalArgumentException("topicFilter == null");
        if (topicFilter.isEmpty()) throw new IllegalArgumentException("topicFilter is empty");

        StringBuilder sb = new StringBuilder();
        String[] splits = split(topicFilter);
        _tokens = new String[splits.length];

        for (int i = 0; i < splits.length; i++) {
            _tokens[i] = splits[i];
            if (_tokens[i].isEmpty()) throw new IllegalArgumentException("Empty item: " + i);
            if (_tokens[i].equals(WILD_TREE) && i < splits.length - 1)
                throw new IllegalArgumentException("Item after #");
            sb.append(_tokens[i]);
            if (i < splits.length - 1) sb.append(DELIM);
        }

        _filter = sb.toString();
    }

    public boolean match(String topicName) {
        if (topicName == null || topicName.isEmpty()) {
            return false;
        }
        if (_filter.equals(WILD_TREE)) {
            return true;
        }
        if (_filter.equals(topicName)) {
            return true;
        }
        String[] splits = split(topicName);
        return match(splits);
    }

    public boolean match(String[] otherTokens) {
        if (otherTokens == null || otherTokens.length ==0) {
            return false;
        }
        if (_filter.equals(WILD_TREE)) {
            return true;
        }
        if (_tokens.length > otherTokens.length) {
            return false;
        }
        for (int i = 0; i < _tokens.length; i++) {
            if (_tokens[i].equals(WILD_TREE)) {
                return true;
            }
            if (_tokens[i].equals(WILD_LEVEL)){
                continue;
            }
            if (!_tokens[i].equals(otherTokens[i])){
                return false;
            }
        }
        return true;
    }

    public static String[] split(String topicName) {
        if (topicName == null || topicName.isEmpty()) {
            return new String[0];
        }
        String[] splits = topicName.split("\\" + DELIM);
        String[] tokens = new String[splits.length];
        for (int i = 0; i < splits.length; i++) {
            tokens[i] = splits[i].trim();
        }
        return tokens;
    }

    @Override
    public String toString() {
        return _filter;
    }
}
