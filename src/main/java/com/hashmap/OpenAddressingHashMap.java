package com.hashmap;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.*;


public class OpenAddressingHashMap<Key,Value> extends AbstractMap<Key,Value> implements Map<Key,Value> {

    private static final int INIT_CAPACITY = 13;
    private int size;
    private int maxSize;

    private boolean isUpdated = false;

    public HashEntry<Key, Value>[] values;
    public Set<Entry<Key,Value>> entrySet;

    private double loadFactor = 0.50f;

    public OpenAddressingHashMap() {
        this(INIT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(int capacity) {
        maxSize = capacity > INIT_CAPACITY ? capacity : INIT_CAPACITY;
        values = new HashEntry[maxSize];
    }

    public int hash(Object key) {
        return (key.hashCode() & 0x7fffffff) % maxSize;
    }

    @Override
    public Value get(Object key) {
        if (isEmpty())
            return null;
        Map.Entry<Key, Value> curEntry;
        for (int i = hash(key); values[i] != null; i = (i+1) % maxSize) {
            curEntry = values[i];
            if (curEntry.getKey().equals(key))
                return curEntry.getValue();
        }
        return null;
    }

    @Override
    public Value put(Key key, Value value) {
        if (key == null)
            return null;
        if (value == null)
            throw new UnsupportedOperationException("Key and Value must not equals null");
        if (size >= maxSize * loadFactor) {
            resize(2 * maxSize);
        }

        int i;
        Map.Entry<Key, Value> curEntry;
        for (i = hash(key); values[i] != null; i = (i + 1) % maxSize) {
            curEntry = values[i];
            if (curEntry.equals(key)) {
                return curEntry.setValue(value);
            }
        }
        values[i] = new HashEntry<Key, Value>(key, value);
        size++;
        isUpdated = true;
        return values[i].getValue();
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        OpenAddressingHashMap<Key, Value> newMap = new OpenAddressingHashMap<Key, Value>(capacity);
        for (int i = 0; i < maxSize; i++) {
            HashEntry curEntry = values[i];
            if (curEntry != null && curEntry.getKey() != null)
                newMap.put((Key) curEntry.getKey(), (Value) curEntry.getValue());
        }
        values = newMap.values;
        maxSize = Math.min(capacity, Integer.MAX_VALUE);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < maxSize; i++) {
            if (values[i] != null && values[i].getValue().equals(value))
                return true;
        }
        return false;
    }

    @Override
    public Value remove(Object key) {
        if (!containsKey(key))
            return null;

        int i = hash(key);
        while (!key.equals(values[i].getKey()) )
            i = (i + 1) % maxSize;

        Value retValue = values[i].getValue();
        values[i] = null;

        i = (i + 1) % maxSize;
        Map.Entry curEntry;
        while (values[i] != null) {
            curEntry = values[i];
            values[i] = null;
            size--;
            put((Key) curEntry.getKey(),(Value) curEntry.getValue());
            i = (i + 1) % maxSize;
        }
        size--;
        isUpdated = true;


        return retValue;
    }

    public void clear() {
        Arrays.fill(values, null);
        size = 0;
    }

    @Override
    public Set<Entry<Key,Value>> entrySet() {
        return isUpdated ? (entrySet = new OpenAddressingEntrySet()) : entrySet;
    }


    private final class OpenAddressingEntrySet extends  AbstractSet<Entry<Key,Value>> {
        public Iterator<Entry<Key,Value>> iterator() {
            return new Iterator<Entry<Key,Value>>() {

                private Entry<Key, Value>[] entries = getNotNullEntries();
                private int i = -1;
                private int size = entries.length;
                private boolean hasNext = false;

                public boolean hasNext() {
                    if (size == 0)
                        return (hasNext = false);
                    int iNext = i+1;
                    if (iNext < size && entries[iNext] == null)
                        throw new IllegalStateException("In not null set exists null value");
                    return (hasNext = iNext < size);
                }

                public Entry<Key,Value> next() {
                    if (!hasNext)
                        throw new NoSuchElementException();
                    // Clear after going next. We can do it, because we have own independence entry instances
                    if (i != -1)
                        entries[i] = null;
                    return entries[++i];
                }

                public void remove() {
                    if (i == -1)
                        throw new IllegalStateException("Before remove() you need to call next()");
                    OpenAddressingHashMap.this.remove(entries[i].getKey());
                    entries[i] = null;
                }
            };
        }

        @Override
        public int size() {
            return OpenAddressingHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return OpenAddressingHashMap.this.isEmpty();
        }

        @Override
        public void clear() {
            OpenAddressingHashMap.this.clear();
        }

        @Override
        public boolean contains(Object k) {
            return OpenAddressingHashMap.this.containsKey(k);
        }

    }

    @SuppressWarnings("unchecked")
    private Entry[] getNotNullEntries() {
        Map.Entry<Key,Value>[] notNullValues = new Map.Entry[size];
        for (int i = 0, j = 0; i < maxSize; i++) {
            if (values[i] != null) {
                notNullValues[j++] = new HashEntry<Key, Value>(values[i]);

            }
        }
        isUpdated = false;
        return notNullValues;
    }

    private void outputMap() {
        for (int i = 0; i < maxSize; i++)
            System.out.println(values[i] == null ? null : values[i].getKey());
    }
}
