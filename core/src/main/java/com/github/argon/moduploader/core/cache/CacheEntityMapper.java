package com.github.argon.moduploader.core.cache;

public interface CacheEntityMapper<Key, Value> {
    CacheEntity<Key, Value> map(Value entity);
}
