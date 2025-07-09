package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.cache.CacheEntity;
import com.github.argon.moduploader.core.db.CrudRepository;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamPoweredStoreClient;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamGameRepoMapper;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamMapper;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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

    @Produces
    @Singleton
    public Steam steam(
        IFileService fileService,
        SteamStoreService storeService,
        SteamMapper steamMapper
    ) {
        return new Steam(fileService, storeService, steamMapper);
    }

    @Produces
    @Singleton
    public SteamStoreService steamStoreService(
        @RestClient SteamStoreClient storeClient,
        @RestClient SteamPoweredStoreClient poweredStoreClient
    ) {
        return new SteamStoreService(storeClient, poweredStoreClient);
    }

    @Produces
    @Singleton
    public SteamGameRepoMapper steamRepoMapper() {
        return new SteamGameRepoMapper();
    }

    @Produces
    @Singleton
    @Named
    public CrudRepository<Long, CacheEntity<Long, SteamGame>> steamFileRepository(
        @DataSource("file-cache") AgroalDataSource fileCacheDatasource,
        SteamGameRepoMapper mapper
    ) {
        return new CrudRepository<>(CACHE_TABLE_NAME, fileCacheDatasource, mapper);
    }

    @Produces
    @Singleton
    @Named
    public CrudRepository<Long, CacheEntity<Long, SteamGame>> steamMemoryRepository(
        @DataSource("mem-cache") AgroalDataSource memoryCacheDatasource,
        SteamGameRepoMapper mapper
    ) {
        return new CrudRepository<>(CACHE_TABLE_NAME, memoryCacheDatasource, mapper);
    }

    @Produces
    @Singleton
    public SteamCachingService steamCachingService(
        @Named("steamMemoryRepository") CrudRepository<Long, CacheEntity<Long, SteamGame>> memoryRepo,
        @Named("steamFileRepository") CrudRepository<Long, CacheEntity<Long, SteamGame>> fileyRepo
    ) {
        return new SteamCachingService(memoryRepo, fileyRepo);
    }
}
