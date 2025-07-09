package com.github.argon.moduploader.core.cache;

import com.github.argon.moduploader.core.db.Entity;
import com.github.argon.moduploader.core.db.Searchable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record CacheEntity<ID, T>(
    ID id,
    String searchable,
    T entry
) implements Entity<ID>, Searchable {
}
