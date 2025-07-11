package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "search-game", description = "Search for games in the Steam Workshop.")
public class SteamSearchGameCommand implements Runnable {
    @CommandLine.ParentCommand
    SteamCommand parentCommand;

    @Inject
    Steam steam;
    @Inject
    CliPrinter cliPrinter;

    @CommandLine.Option(names = {"-n", "--name"},
        description = "Part of the name")
    String name;

    @Override
    public void run() {
        String apiKey = parentCommand.apiKey;
        // FIXME apiKey muss übergeben werden, wenn nicht in properties
        List<SteamGame> steamGames = steam.store().searchGames(name);
        cliPrinter.printSteamGames(steamGames);
    }
}
