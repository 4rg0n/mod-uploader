package com.github.argon.moduploader.core.cache;

import com.github.argon.moduploader.core.db.RepoMapper;

public interface CacheRepoMapper<ID, T> extends RepoMapper<ID, CacheEntity<ID, T>> {
}
