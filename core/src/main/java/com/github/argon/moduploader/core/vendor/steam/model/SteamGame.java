package com.github.argon.moduploader.core.vendor.steam.model;

import java.io.Serializable;

public record SteamGame (
    Long id,
    String name
) implements Serializable {}
