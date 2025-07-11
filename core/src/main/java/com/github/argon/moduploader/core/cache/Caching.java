package com.github.argon.moduploader.core.cache;

import com.github.argon.moduploader.core.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface Caching<Key, Value> {
    void setCacheDataSupplier(Supplier<List<Value>> cacheDataSupplier);
    List<Key> getKeys();
    Optional<Value> get(Key key);
    int cache(Value value);
    int cache(List<Value> values);
    List<Value> getAll();
    Page<Value> getAll(Pageable pageable);
    List<Key> getMissing(List<Key> gameIds);
    List<Value> get(List<Key> keys);
    Page<Value> get(List<Key> gameIds, Pageable pageable);
    List<Value> find(String searchTerm);
    Page<Value> find(String searchTerm, Pageable pageable);
    void invalidateAll();
    void invalidate(Key key);
    int persist();
    int load();
    int size();
    int reload();
    boolean isEmpty();
}
