package com.github.argon.moduploader.core.vendor.steam.mapper;

import com.github.argon.moduploader.core.vendor.steam.api.SteamApiException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 * Handles and maps JSON api errors thrown by the mod.io
 */
@Provider
public class SteamApiErrorMapper implements ResponseExceptionMapper<SteamApiException> {

    @Override
    public SteamApiException toThrowable(Response response) {
        if (!response.hasEntity()) {
            return new SteamApiException(response.getStatusInfo().toEnum(), response);
        }

        return new SteamApiException(response.getStatusInfo().toEnum(), response);
    }
}
