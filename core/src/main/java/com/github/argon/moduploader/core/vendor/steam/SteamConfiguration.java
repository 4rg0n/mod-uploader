package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.cache.*;
import com.github.argon.moduploader.core.db.CrudRepository;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamAppsRestClient;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStore;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreRestClient;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamAPIMapper;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamCacheMapper;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamMapper;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

/**
 * Contexts and Dependency Injection config for Quarkus
 * Here the code for gluing all the classes needed for Steam together lives.
 */
@ApplicationScoped
public class SteamConfiguration {
    public final static String STEAM_APP_ID_TXT = "steam_appid.txt";
    public final static Integer DEFAULT_APP_ID = 480;
    public final static String DEFAULT_APP_ID_STRING = "480";
    public static final String CACHE_TABLE_NAME = "STEAM_GAME";

    @Inject SteamProperties steamProperties;

    @Produces
    @Singleton
    public Steam steam(
        IFileService fileService,
        SteamStoreService storeService,
        SteamAPIMapper steamAPIMapper
    ) {
        return new Steam(fileService, storeService, steamAPIMapper);
    }

    @Produces
    @Singleton
    public SteamStoreService steamStoreService(
        @Named("steamCachingService") Caching<Long, SteamGame> cachingService
    ) {
        return new SteamStoreService(cachingService);
    }

    @Produces
    @Singleton
    public SteamStore steamStore(
        @RestClient SteamAppsRestClient storeClient,
        @RestClient SteamStoreRestClient poweredStoreClient
    ) {
        return new SteamStore(storeClient, poweredStoreClient);
    }

    @Produces
    @Singleton
    public SteamCacheMapper steamCacheMapper() {
        return new SteamCacheMapper();
    }

    @Produces
    @Singleton
    @Named
    public CrudRepository<Long, CacheEntity<Long, SteamGame>> steamFileRepository(
        @DataSource("file-cache") AgroalDataSource fileCacheDatasource,
        CacheRepoMapper<Long, SteamGame> mapper
    ) {
        return new CrudRepository<>(CACHE_TABLE_NAME, fileCacheDatasource, mapper);
    }

    @Produces
    @Singleton
    @Named
    public CrudRepository<Long, CacheEntity<Long, SteamGame>> steamMemoryRepository(
        @DataSource("mem-cache") AgroalDataSource memoryCacheDatasource,
        CacheRepoMapper<Long, SteamGame> mapper
    ) {
        return new CrudRepository<>(CACHE_TABLE_NAME, memoryCacheDatasource, mapper);
    }

    @Produces
    @Singleton
    @Named
    public Caching<Long, SteamGame> steamCachingService(
        @Named("steamMemoryRepository") CrudRepository<Long, CacheEntity<Long, SteamGame>> memoryRepo,
        @Named("steamFileRepository") CrudRepository<Long, CacheEntity<Long, SteamGame>> fileyRepo,
        SteamStore steamStore,
        SteamMapper steamMapper,
        CacheEntityMapper<Long, SteamGame> steamCacheMapper,
        SteamProperties steamProperties
    ) {
        return new CachingService<>(memoryRepo, fileyRepo, steamCacheMapper, () ->
            // FIXME: only works for api key in properties
            steamProperties.apiKey().map(apiKey ->
                steamStore.getAllGames(apiKey).stream()
                    .map(steamMapper::map)
                    .toList()
            ).orElse(List.of())
        );
    }
}
