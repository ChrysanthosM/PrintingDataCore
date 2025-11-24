package org.masouras.data.control;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class OneTimeCache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();

    public V computeOrGetOnce(K key, Supplier<V> supplier) {
        V existing = cache.remove(key);
        if (existing != null) return existing;

        V newValue = supplier.get();
        cache.put(key, newValue);
        return newValue;
    }
}

