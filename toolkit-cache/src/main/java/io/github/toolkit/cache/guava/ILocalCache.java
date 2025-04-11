package io.github.toolkit.cache.guava;

public interface ILocalCache<K, V> {

    V get(K key);

    void refresh(K key);

    void put(K key, V value);
}
