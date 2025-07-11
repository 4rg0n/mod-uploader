package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.Page;
import com.github.argon.moduploader.core.cache.Caching;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class SteamStoreService {
    private final Caching<Long, SteamGame> cachingService;

    public Page<SteamGame> getGames(Pageable pageable) {
        return cachingService.getAll(pageable);
    }

    public List<SteamGame> searchGames(String searchTerm) {
        return cachingService.find(searchTerm);
    }

    public Page<SteamGame> searchGames(String searchTerm, Pageable pageable) {
        return cachingService.find(searchTerm, pageable);
    }
}
