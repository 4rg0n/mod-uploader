package com.github.argon.moduploader.core.vendor.steam.api.dto;

public record SteamDetailedAppDto(
    Long steamAppId,
    String name,
    Integer lastModified,
    Integer priceChangeNumber
) {}
