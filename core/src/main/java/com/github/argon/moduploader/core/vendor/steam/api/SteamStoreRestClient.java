package com.github.argon.moduploader.core.vendor.steam.api;

import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamDetailedAppDto;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamApiErrorMapper;
import com.github.argon.moduploader.core.vendor.steam.mapper.SteamDtoMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient
@RegisterProvider(SteamApiErrorMapper.class)
@RegisterProvider(SteamDtoMapper.class)
public interface SteamStoreRestClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/appdetails")
    List<SteamDetailedAppDto> getApps(
        @QueryParam("apids") List<Long> appIds
    );
}
