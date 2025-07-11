package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.modio.Modio;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "delete", description = "Will archive a mod on mod.io. You can only completely delete a mod through the WebUI.")
public class ModioDeleteCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Inject Modio modio;
    @Inject ModioLoginCommand loginCommand;

    @CommandLine.Option(names = {"-id", "--mod-id"}, required = true,
        description = "Id of the mod.")
    Long modId;

    @Override
    public Integer call() {
        Long gameId = parentCommand.gameId;
        BearerToken bearerToken = modio.authService().getBearerToken();

        // force login
        while (bearerToken == null || bearerToken.isExpired()) {
            new CommandLine(loginCommand).execute( "-e");
            bearerToken = modio.authService().getBearerToken();
        }

        if (!modio.archiveMod(gameId, modId)) {
            return 1;
        }

        return 0;
    }
}
