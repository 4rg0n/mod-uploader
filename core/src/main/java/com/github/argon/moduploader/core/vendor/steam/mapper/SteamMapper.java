package com.github.argon.moduploader.core.vendor.steam.mapper;

import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamDetailedAppDto;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SteamMapper {
    @Mapping(target = "id", source = "steamAppId")
    SteamGame map(SteamDetailedAppDto steamAppDto);
}
