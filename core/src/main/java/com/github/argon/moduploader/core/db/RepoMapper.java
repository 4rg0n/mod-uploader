package com.github.argon.moduploader.core.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RepoMapper<ID, T> {
    void mapStatement(PreparedStatement statement, T entity) throws SQLException;
    Optional<T> mapResult(ResultSet rs) throws SQLException;
    List<T> mapResultList(ResultSet rs) throws SQLException;
    void mapStatementIdIn(PreparedStatement statement, int idx, ID id) throws SQLException;
    ID mapResultId(ResultSet resultSet) throws SQLException;
    void mapStatementId(PreparedStatement statement , ID id) throws SQLException;
}
