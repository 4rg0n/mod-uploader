package com.github.argon.moduploader.core.db;

import io.agroal.api.AgroalDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CrudRepository<ID, T extends Entity<ID>> implements Repository<ID, T>, AutoCloseable {
    @Getter
    private Connection connection;
    @Getter
    private final String tableName;
    private final AgroalDataSource dataSource;
    @Getter
    private final RepoMapper<ID, T> mapper;

    public Connection connect() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
        }

        return connection;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public List<T> findByLike(String searchTerm) {
        String sql = String.format("SELECT * FROM %s WHERE SEARCHABLE LIKE '?'", getTableName());

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            return getMapper().mapList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException("Error selecting like", e);
        }
    }

    @Override
    public int save(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        T firstEntity = entities.getFirst();
        String sql = String.format("MERGE INTO %s VALUES (%s)", tableName, Arrays.stream(firstEntity.getClass().getFields())
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            for (T entity : entities) {
                if (entity.id() == null) {
                    continue; // skip
                }

                mapper.map(statement, entity);
                statement.addBatch();
            }

            return Arrays.stream(statement.executeBatch()).sum();
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting", e);
        }
    }

    public List<T> findByIds(List<ID> ids) {
        String sql = String.format("SELECT * FROM %s WHERE ID IN (%s)", tableName, ids.stream()
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            for (int i = 1; i <= ids.size(); i++) {
                ID id = ids.get(i - 1);
                mapper.mapIdIn(statement, i, id);
            }
            ResultSet resultSet = statement.executeQuery();

            return mapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException("Error selecting", e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = String.format("SELECT * FROM %s ORDER BY ID ASC", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();
            return mapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException("Error selecting all", e);
        }
    }

    @Override
    public List<ID> ids() {
        String sql = String.format("SELECT ID FROM %s ORDER BY ID ASC", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();
            List<ID> ids = new ArrayList<>();
            while(resultSet.next()) {
                ID id = mapper.mapId(resultSet);
                ids.add(id);
            }

            return ids;
        } catch (SQLException e) {
            throw new DatabaseException("Error selecting games", e);
        }
    }

    @Override
    public int save(T entity) {
        String sql = String.format("MERGE INTO %s VALUES (%s)", tableName, Arrays.stream(entity.getClass().getFields())
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            mapper.map(statement, entity);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting games", e);
        }
    }

    @Override
    public int delete(T entity) {
        return deleteById(entity.id());
    }

    @Override
    public int deleteById(ID id) {
        String sql = String.format("DELETE FROM %s WHERE ID = ?", tableName);

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            mapper.mapId(statement, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting by id " + id , e);
        }
    }

    @Override
    public int delete(List<T> entities) {
        String sql = String.format("DELETE FROM %s WHERE ID IN (%s)", tableName, entities.stream()
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            for (int i = 1; i <= entities.size(); i++) {
                mapper.mapIdIn(statement, i, entities.get(i - 1).id());
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting", e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = String.format("DELETE FROM %s", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting all", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE ID = ?", tableName);

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            mapper.mapId(statement, id);
            ResultSet resultSet = statement.executeQuery();

            return mapper.map(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException("Error selecting games", e);
        }
    }
}
