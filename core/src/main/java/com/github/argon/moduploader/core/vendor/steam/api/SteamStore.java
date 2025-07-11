package com.github.argon.moduploader.core.vendor.steam.api;

import com.github.argon.moduploader.core.Chunks;
import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamDetailedAppDto;
import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamSimpleAppsDto;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SteamStore {
    private final SteamAppsRestClient storeClient;
    private final SteamStoreRestClient poweredStoreClient;
    private final Duration readDelay;
    private final int readChunkSize;

    public SteamStore(SteamAppsRestClient storeClient, SteamStoreRestClient poweredStoreClient) {
        this(storeClient, poweredStoreClient, Duration.ofSeconds(1), 200);
    }

    public List<SteamDetailedAppDto> getAllGames(String apiKey) {
        List<SteamSimpleAppsDto.App> simpleApps = getSimpleGames(apiKey);
        List<Long> allGameIds = simpleApps.stream()
            .map(SteamSimpleAppsDto.App::appid)
            .toList();

        return getGames(allGameIds);
    }

    private List<SteamSimpleAppsDto.App> getSimpleGames(String apiKey) {
        return storeClient.getApps(
            apiKey,
            true,
            false,
            true,
            false,
            false,
            null,
            null
        ).appList().apps();
    }

    private List<SteamDetailedAppDto> getGames(List<Long> gameIds) {
        // iterate chunk wise with a delay between each
        // so we do not overload the steam API as much
        List<SteamDetailedAppDto> steamGames = new ArrayList<>();
        Chunks<Long> chunks = new Chunks<>(gameIds, readChunkSize, readDelay);
        chunks.forEach(chunk -> {
            List<SteamDetailedAppDto> steamGameChunk = poweredStoreClient.getApps(chunk);
            steamGames.addAll(steamGameChunk);
        });

        return steamGames;
    }
}
