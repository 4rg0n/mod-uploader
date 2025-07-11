package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerTokenFileConsumer;
import com.github.argon.moduploader.core.auth.BearerTokenFileSupplier;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.modio.api.ModioGameRestClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioModsRestClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioOAuthRestClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioUserRestClient;
import com.github.argon.moduploader.core.vendor.modio.mapper.ModioMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.Validator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.nio.file.Path;

/**
 * Contexts and Dependency Injection config for Quarkus
 * Here the code for gluing all the classes needed for mod.io together lives.
 *
 */
@ApplicationScoped
public class ModioConfiguration {
    public final static String REMOTE_MOD_CACHE = "ModioMod.Remote";

    @Inject
    ModioProperties modioProperties;

    @Singleton
    @Produces
    public Modio modio(
        ModioModService storeService,
        ModioUserService userService,
        ModioAuthService authService,
        ModioGameService gameService
    ) {
        return new Modio(storeService, userService, authService, gameService);
    }

    @Singleton
    @Produces
    public BearerTokenFileConsumer bearerTokenFileConsumer(IFileService fileService) {
        Path tokenFilePath = modioProperties.tokenFilePath();
        return new BearerTokenFileConsumer(tokenFilePath, fileService);
    }

    @Singleton
    @Produces
    public BearerTokenFileSupplier bearerTokenFileProvider(IFileService fileService) {
        Path tokenFilePath = modioProperties.tokenFilePath();
        return new BearerTokenFileSupplier(tokenFilePath, fileService);
    }

    @Singleton
    @Produces
    public ModioAuthService modioAuthService(
        @RestClient ModioOAuthRestClient modioAuthClient,
        BearerTokenFileSupplier bearerTokenProvider,
        BearerTokenFileConsumer bearerTokenConsumer
    ) {
        return new ModioAuthService(modioAuthClient, bearerTokenProvider, bearerTokenConsumer);
    }

    @Singleton
    @Produces
    public ModioUserService modioUserService(
        @RestClient ModioUserRestClient modioUserRestClient,
        ModioMapper modioMapper
    ) {
        return new ModioUserService(modioUserRestClient, modioMapper);
    }

    @Singleton
    @Produces
    public ModioGameService modioGameService(
        @RestClient ModioGameRestClient modioGameRestClient,
        ModioMapper modioMapper
    ) {
        return new ModioGameService(modioGameRestClient, modioMapper);
    }

    @Singleton
    @Produces
    public ModioModService modioStoreService(
        @RestClient ModioModsRestClient modioModsRestClient,
        ModioMapper  modioMapper,
        IFileService fileService,
        BearerTokenFileSupplier bearerTokenProvider,
        Validator validator
    ) {
        return new ModioModService(modioModsRestClient, modioMapper, fileService, bearerTokenProvider, validator);
    }
}
