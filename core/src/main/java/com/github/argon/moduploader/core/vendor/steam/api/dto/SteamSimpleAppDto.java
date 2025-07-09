package com.github.argon.moduploader.core.vendor.steam.api.dto;

public record SteamSimpleAppDto(
    Integer appid,
    String name,
    Integer lastModified,
    Integer priceChangeNumber
) {
}
