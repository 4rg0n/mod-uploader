package com.github.argon.moduploader.core.db;

import com.github.argon.moduploader.core.Page;
import io.agroal.api.AgroalDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

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
    private Connection connection;
    @Getter
    private final String tableName;
    private final AgroalDataSource dataSource;
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

    @Override
    public List<T> findByLike(String searchTerm) {
        String sql = String.format("SELECT * FROM %s WHERE SEARCHABLE LIKE '?'", getTableName());

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            return mapper.mapResultList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Page<T> findByLike(String searchTerm, Pageable pageable) {
        String totalRowsSql = String.format("SELECT COUNT(ID) FROM %s WHERE SEARCHABLE LIKE '?' OFFSET ? LIMIT ?", getTableName());
        String sql = String.format("SELECT * FROM %s WHERE SEARCHABLE LIKE '?' OFFSET ? LIMIT ?", getTableName());

        try (
            Connection connection = connect();
            PreparedStatement totalRowsStatement =  connection.prepareStatement(totalRowsSql);
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            totalRowsStatement.setString(1, "%" + searchTerm + "%");
            totalRowsStatement.setLong(2, pageable.getOffset());
            totalRowsStatement.setLong(3, pageable.getOffset() + pageable.getPageSize());
            ResultSet totalRowsResult = totalRowsStatement.executeQuery();
            long totalRows = totalRowsResult.next() ? totalRowsResult.getLong(1) : 0;

            statement.setString(1, "%" + searchTerm + "%");
            statement.setLong(2, pageable.getOffset());
            statement.setLong(3, pageable.getOffset() + pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<T> entities = mapper.mapResultList(resultSet);

            return Page.of(entities, pageable, totalRows);
        } catch (SQLException e) {
            throw new DatabaseException(e);
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

                mapper.mapStatement(statement, entity);
                statement.addBatch();
            }

            return Arrays.stream(statement.executeBatch()).sum();
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
                mapper.mapStatementIdIn(statement, i, id);
            }
            ResultSet resultSet = statement.executeQuery();

            return mapper.mapResultList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Page<T> findByIds(List<ID> ids, Pageable pageable) {
        String totalRowsSql = String.format("SELECT COUNT(ID) FROM %s WHERE ID IN (%s) OFFSET ? LIMIT ?", tableName, ids.stream()
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        String sql = String.format("SELECT * FROM %s WHERE ID IN (%s) OFFSET ? LIMIT ?", tableName, ids.stream()
            .map(v -> "?")
            .collect(Collectors.joining(", ")));

        try (
            Connection connection = connect();
            PreparedStatement totalRowsStatement =  connection.prepareStatement(totalRowsSql);
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            for (int i = 1; i <= ids.size(); i++) {
                ID id = ids.get(i - 1);
                mapper.mapStatementIdIn(statement, i, id);
                mapper.mapStatementIdIn(totalRowsStatement, i, id);
            }

            totalRowsStatement.setLong(ids.size() + 1, pageable.getOffset());
            totalRowsStatement.setLong(ids.size() + 2, pageable.getOffset() + pageable.getPageSize());
            ResultSet totalRowsResult = totalRowsStatement.executeQuery();
            long totalRows = totalRowsResult.next() ? totalRowsResult.getLong(1) : 0;

            statement.setLong(ids.size() + 1, pageable.getOffset());
            statement.setLong(ids.size() + 2, pageable.getOffset() + pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            List<T> entities = mapper.mapResultList(resultSet);

            return Page.of(entities, pageable, totalRows);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        String sql = String.format("SELECT * FROM %s OFFSET ? LIMIT ?", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            statement.setLong(1, pageable.getOffset());
            statement.setLong(2, pageable.getOffset() + pageable.getPageSize());

            ResultSet resultSet = statement.executeQuery();
            List<T> entities = mapper.mapResultList(resultSet);

            return Page.of(entities, pageable, count());
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = String.format("SELECT * FROM %s", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();
            return mapper.mapResultList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<ID> ids() {
        String sql = String.format("SELECT ID FROM %s", tableName);
        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();
            List<ID> ids = new ArrayList<>();
            while(resultSet.next()) {
                ID id = mapper.mapResultId(resultSet);
                ids.add(id);
            }

            return ids;
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
            mapper.mapStatement(statement, entity);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
            mapper.mapStatementId(statement, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
                mapper.mapStatementIdIn(statement, i, entities.get(i - 1).id());
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE ID = ?", tableName);

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            mapper.mapStatementId(statement, id);
            ResultSet resultSet = statement.executeQuery();

            return mapper.mapResult(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public int count() {
        String sql = String.format("SELECT COUNT(ID) FROM %s ", tableName);

        try (
            Connection connection = connect();
            PreparedStatement statement =  connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return 0;
            }

            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return count() == 0;
    }
}
