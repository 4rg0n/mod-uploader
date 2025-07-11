package com.github.argon.moduploader.core.vendor.steam.api.dto;

import java.util.List;

public record SteamSimpleAppsDto(
   AppList appList
) {
    public record AppList(
        List<App> apps
    ) {
    }

    public record App(
        Long appid,
        String name
    ) {}
}
