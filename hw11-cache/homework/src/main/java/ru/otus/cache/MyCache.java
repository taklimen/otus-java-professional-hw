package ru.otus.cache;


import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private final WeakHashMap<K, V> entityMap;
    private final List<HwListener<K, V>> listeners;

    public MyCache() {
        this.entityMap = new WeakHashMap<>();
        this.listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        entityMap.put(key, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        V value = entityMap.remove(key);
        notifyListeners(key, value, "remove");
    }

    private void notifyListeners(K key, V value, String action) {
        listeners.forEach(listener -> listener.notify(key, value, action));
    }

    @Override
    public V get(K key) {
        V value = entityMap.get(key);
        notifyListeners(key, value, "get");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
