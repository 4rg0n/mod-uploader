package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.cache.CacheEntity;
import com.github.argon.moduploader.core.cache.Caching;
import com.github.argon.moduploader.core.db.CrudRepository;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SteamCachingService implements Caching<Long, SteamGame> {
    private final CrudRepository<Long, CacheEntity<Long, SteamGame>> memoryGameRepo;
    private final CrudRepository<Long, CacheEntity<Long, SteamGame>> fileGameRepo;

    @Override
    public Optional<SteamGame> get(Long gameId) {
        return memoryGameRepo.findById(gameId)
            .map(CacheEntity::entry);
    }

    @Override
    public int cache(SteamGame game) {
        return memoryGameRepo.save(new CacheEntity<>(game.id(), game.name(), game));
    }

    @Override
    public int cache(List<SteamGame> games) {
        List<CacheEntity<Long, SteamGame>> cacheEntities = games.stream()
            .map(steamGame -> new CacheEntity<>(steamGame.id(), steamGame.name(), steamGame))
            .toList();

        return memoryGameRepo.save(cacheEntities);
    }

    @Override
    public List<SteamGame> getAll() {
        return memoryGameRepo.findAll().stream().map(
            CacheEntity::entry
        ).toList();
    }

    @Override
    public List<SteamGame> get(List<Long> gameIds) {
        return memoryGameRepo.findByIds(gameIds).stream().map(
            CacheEntity::entry
        ).toList();
    }

    @Override
    public List<SteamGame> find(String namePart) {
        return memoryGameRepo.findByLike(namePart).stream().map(
            CacheEntity::entry
        ).toList();
    }

    @Override
    public void invalidateAll() {
        memoryGameRepo.deleteAll();
        memoryGameRepo.deleteAll();
    }

    @Override
    public void invalidate(Long id) {
        memoryGameRepo.deleteById(id);
        fileGameRepo.deleteById(id);
    }

    @Override
    public int persist() {
        return fileGameRepo.save(memoryGameRepo.findAll());
    }

    @Override
    public int load() {
        return memoryGameRepo.save(fileGameRepo.findAll());
    }
}
