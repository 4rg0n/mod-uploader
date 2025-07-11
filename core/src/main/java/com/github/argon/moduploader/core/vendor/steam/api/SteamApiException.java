package com.github.argon.moduploader.core.vendor.steam.api;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SteamApiException extends RuntimeException {
    private final Response.Status code;
    private final Response response;
}
