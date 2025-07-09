package com.github.argon.moduploader.core.cache;

import java.util.List;
import java.util.Optional;

public interface Caching<Key, Value> {
    Optional<Value> get(Key key);
    int cache(Value value);
    int cache(List<Value> values);
    List<Value> getAll();
    List<Value> get(List<Key> keys);
    List<Value> find(String searchTerm);
    void invalidateAll();
    void invalidate(Key key);
    int persist();
    int load();
}
