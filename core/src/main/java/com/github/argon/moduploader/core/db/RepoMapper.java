package com.github.argon.moduploader.core.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RepoMapper<ID, T> {
    void map(PreparedStatement statement, T entity) throws SQLException;
    Optional<T> map(ResultSet rs) throws SQLException;
    List<T> mapList(ResultSet rs) throws SQLException;
    void mapIdIn(PreparedStatement statement, int idx, ID id) throws SQLException;
    ID mapId(ResultSet resultSet) throws SQLException;
    void mapId(PreparedStatement statement , ID id) throws SQLException;
}
