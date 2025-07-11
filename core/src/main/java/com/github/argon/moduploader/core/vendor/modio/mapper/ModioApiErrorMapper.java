package com.github.argon.moduploader.core.vendor.modio.mapper;

import com.github.argon.moduploader.core.vendor.modio.api.ModioApiException;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioApiErrorDto;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 * Handles and maps JSON api errors thrown by the mod.io
 */
public class ModioApiErrorMapper implements ResponseExceptionMapper<ModioApiException> {

    @Override
    public ModioApiException toThrowable(Response response) {
        if (!response.hasEntity()) {
            return new ModioApiException(null, response.getStatusInfo().toEnum(), response);
        }

        ModioApiErrorDto modioApiErrorDto = response.readEntity(ModioApiErrorDto.class);
        return new ModioApiException(modioApiErrorDto, response.getStatusInfo().toEnum(), response);
    }
}
