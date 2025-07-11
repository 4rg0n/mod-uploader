package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.cache.Caching;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class SteamEventConfiguration {
    @Named("steamCachingService")
    @Inject Caching<Long, SteamGame> steamCachingService;

    void onShutdown(@Observes ShutdownEvent event) {
        steamCachingService.persist();
    }

    void onStart(@Observes StartupEvent event) {
        steamCachingService.load();
    }
}
