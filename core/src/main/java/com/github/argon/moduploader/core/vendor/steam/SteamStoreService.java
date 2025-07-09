package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.vendor.steam.api.SteamPoweredStoreClient;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamSimpleAppDto;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SteamStoreService {
    private final SteamStoreClient storeClient;
    private final SteamPoweredStoreClient poweredStoreClient;

    public List<SteamGame> getApps(String apiKey) {
        List<SteamSimpleAppDto> simpleApps = storeClient.getApps(
            apiKey,
            true,
            false,
            true,
            false,
            false,
            null,
            1000
        );


        return null;
    }
}
