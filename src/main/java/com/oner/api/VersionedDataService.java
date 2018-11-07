package com.oner.api;

import java.io.Closeable;

public interface VersionedDataService<K, V, T> extends Closeable {

    V getCurrentConfig(K key);

    V getConfig(K key, T effectiveSince);

    V getLatestConfig(K key);

    void storeConfig(K key, V value);

    void loadData(int maxEntry, int configPerKey);

    void readData(int maxEntry, int configPerKey);
}