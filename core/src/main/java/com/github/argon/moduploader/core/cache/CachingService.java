package com.github.argon.moduploader.core.cache;

import com.github.argon.moduploader.core.Page;
import com.github.argon.moduploader.core.db.DatabaseException;
import com.github.argon.moduploader.core.db.Repository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import javax.sql.rowset.serial.SerialException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@AllArgsConstructor
public class CachingService<Key, Value> implements Caching<Key, Value> {
    private final Repository<Key, CacheEntity<Key, Value>> memoryRepo;
    private final Repository<Key, CacheEntity<Key, Value>> fileRepo;
    private final CacheEntityMapper<Key, Value> mapper;
    @Setter
    private Supplier<List<Value>> cacheDataSupplier;

    @Override
    public Optional<Value> get(Key gameId) {
        try {
            return memoryRepo.findById(gameId)
                .map(CacheEntity::entry);
        } catch (DatabaseException e) {
            handleException(e);
            return get(gameId);
        }
    }

    @Override
    public List<Value> get(List<Key> gameIds) {
        try {
            return memoryRepo.findByIds(gameIds).stream().map(
                CacheEntity::entry
            ).toList();
        } catch (DatabaseException e) {
            handleException(e);
            return get(gameIds);
        }
    }

    @Override
    public Page<Value> get(List<Key> gameIds, Pageable pageable) {
        try {
            Page<CacheEntity<Key, Value>> cacheEntries = memoryRepo.findByIds(gameIds, pageable);
            List<Value> games = cacheEntries.stream()
                .map(CacheEntity::entry)
                .toList();

            return Page.of(games, pageable, games.size());
        } catch (DatabaseException e) {
            handleException(e);
            return get(gameIds,pageable);
        }
    }

    @Override
    public int cache(Value game) {
        return memoryRepo.save(mapper.map(game));
    }

    @Override
    public int cache(List<Value> games) {
        List<CacheEntity<Key, Value>> cacheEntities = games.stream()
            .map(mapper::map)
            .toList();

        return memoryRepo.save(cacheEntities);
    }

    @Override
    public List<Value> getAll() {
        try {
            return memoryRepo.findAll().stream().map(
                CacheEntity::entry
            ).toList();
        } catch (DatabaseException e) {
            handleException(e);
            return getAll();
        }
    }

    @Override
    public Page<Value> getAll(Pageable pageable) {
        try {
            Page<CacheEntity<Key, Value>> cacheEntries = memoryRepo.findAll(pageable);
            List<Value> games = cacheEntries.stream()
                .map(CacheEntity::entry)
                .toList();

            return Page.of(games,  pageable, games.size());
        } catch (DatabaseException e) {
            handleException(e);
            return getAll(pageable);
        }
    }

    public List<Key> getMissing(List<Key> gameIds) {
        List<Key> ids = memoryRepo.ids();
        return ids.stream()
            .filter(gameIds::contains)
            .toList();
    }

    @Override
    public List<Value> find(String searchTerm) {
        try {
            return memoryRepo.findByLike(searchTerm).stream().map(
                CacheEntity::entry
            ).toList();
        } catch (DatabaseException e) {
            handleException(e);
            return find(searchTerm);
        }
    }

    @Override
    public Page<Value> find(String searchTerm, Pageable pageable) {
        try {
            Page<CacheEntity<Key, Value>> cacheEntries = memoryRepo.findByLike(searchTerm, pageable);
            List<Value> games = cacheEntries.stream()
                .map(CacheEntity::entry)
                .toList();

            return Page.of(games,  pageable, games.size());
        } catch (DatabaseException e) {
            handleException(e);
            return find(searchTerm, pageable);
        }
    }

    @Override
    public void invalidateAll() {
        log.debug("Invalidating all cache entries");
        memoryRepo.deleteAll();
        memoryRepo.deleteAll();
    }

    @Override
    public void invalidate(Key id) {
        log.debug("Invalidate cache entry with id {}", id);
        memoryRepo.deleteById(id);
        fileRepo.deleteById(id);
    }

    @Override
    public int persist() {
        log.debug("Persisting cache entries");
        return fileRepo.save(memoryRepo.findAll());
    }

    @Override
    public int load() {
        if (fileRepo.isEmpty()) {
            log.debug("Load cache from supplier.");
            return cache(cacheDataSupplier.get());
        }

        log.debug("Load cache from file.");
        return memoryRepo.save(fileRepo.findAll());
    }

    @Override
    public boolean isEmpty() {
        return memoryRepo.isEmpty();
    }

    @Override
    public int size() {
        return memoryRepo.count();
    }

    @Override
    public List<Key> getKeys() {
        return memoryRepo.ids();
    }

    @Override
    public int reload() {
        log.debug("Reloading cache entries");
        invalidateAll();
        return load();
    }

    private void handleException(DatabaseException e) {
        Throwable cause = e.getCause();

        if (cause instanceof SerialException || cause instanceof ClassCastException) {
            log.warn("Error while serializing or de-serializing cache entry.", cause);
            reload();
            return;
        }

        throw e;
    }
}
