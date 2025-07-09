package com.github.argon.moduploader.core.db;

import java.util.List;
import java.util.Optional;

public interface Repository<ID, T extends Entity<ID>> {
    List<T> findAll();
    int save(T entity);
    int save(List<T> entity);
    int delete(T entity);
    int deleteById(ID id);
    int delete(List<T> entity);
    int deleteAll();
    List <T> findByIds(List<ID> ids);
    Optional<T> findById(ID id);
    List<ID> ids();
}
