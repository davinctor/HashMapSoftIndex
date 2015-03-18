package com.hashmap;

import java.util.Map;

public class HashEntry<Key, Value> implements Map.Entry<Key, Value> {

    private Key     key;
    private Value   value;

    public HashEntry(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public Value setValue(Value value) {
        Value oldValue = this.value;
        this.value = value;
        return oldValue;
    }
}
