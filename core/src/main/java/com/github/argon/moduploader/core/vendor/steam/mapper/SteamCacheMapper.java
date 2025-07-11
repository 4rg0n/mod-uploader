package com.github.argon.moduploader.core.vendor.steam.mapper;

import com.github.argon.moduploader.core.cache.CacheEntity;
import com.github.argon.moduploader.core.cache.CacheEntityMapper;
import com.github.argon.moduploader.core.cache.CacheRepoMapper;
import com.github.argon.moduploader.core.vendor.steam.model.SteamGame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SteamCacheMapper implements CacheRepoMapper<Long, SteamGame>, CacheEntityMapper<Long, SteamGame> {
    @Override
    public void mapStatement(PreparedStatement statement, CacheEntity<Long, SteamGame> entity) throws SQLException {
        statement.setLong(1, entity.id());
        statement.setString(2, entity.searchable());
        statement.setObject(3, entity.entry());
    }

    @Override
    public Optional<CacheEntity<Long, SteamGame>> mapResult(ResultSet resultSet) throws SQLException {
        if (!resultSet.isBeforeFirst()) {
            return Optional.empty();
        }

        long id = resultSet.getLong(1);
        String name = resultSet.getString(2);
        SteamGame game = resultSet.getObject("GAME", SteamGame.class);

        return Optional.of(new CacheEntity<>(id, name, game));
    }

    @Override
    public List<CacheEntity<Long, SteamGame>> mapResultList(ResultSet resultSet) throws SQLException {
        List<CacheEntity<Long, SteamGame>> games = new ArrayList<>();
        while(resultSet.next()) {
            mapResult(resultSet).ifPresent(games::add);
        }

        return games;
    }

    @Override
    public void mapStatementIdIn(PreparedStatement statement, int idx, Long id) throws SQLException {
        statement.setLong(idx, id);
    }

    @Override
    public Long mapResultId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong(1);
    }

    @Override
    public void mapStatementId(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    @Override
    public CacheEntity<Long, SteamGame> map(SteamGame game) {
        return new CacheEntity<>(game.id(), game.name(), game);
    }
}
