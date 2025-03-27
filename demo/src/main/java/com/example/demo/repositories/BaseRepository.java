package com.example.demo.common.repository;

import com.example.demo.common.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Serializable> 
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    default List<T> findAllActive() {
        return findAll().stream()
                .filter(BaseEntity::getActive)
                .toList();
    }
    default Optional<T> findByIdAndActive(ID id) {
        return findById(id)
                .filter(BaseEntity::getActive);
    }
    default void softDelete(ID id) {
        findById(id).ifPresent(entity -> {
            entity.setActive(false);
            save(entity);
        });
    }
    default void restore(ID id) {
        findById(id).ifPresent(entity -> {
            entity.setActive(true);
            save(entity);
        });
    }
}