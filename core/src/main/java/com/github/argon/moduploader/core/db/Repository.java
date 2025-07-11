package com.github.argon.moduploader.core.db;

import com.github.argon.moduploader.core.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface Repository<ID, T extends Entity<ID>> {
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    int save(T entity);
    int save(List<T> entity);
    int delete(T entity);
    int deleteById(ID id);
    int delete(List<T> entity);
    int deleteAll();
    List <T> findByIds(List<ID> ids);
    Page<T> findByIds(List<ID> ids, Pageable pageable);
    List<T> findByLike(String searchTerm);
    Page<T> findByLike(String searchTerm, Pageable pageable);
    Optional<T> findById(ID id);
    List<ID> ids();
    int count();
    boolean isEmpty();
}
