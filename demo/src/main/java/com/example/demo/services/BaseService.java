package com.example.demo.common.service;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.repository.BaseRepository;
import com.example.demo.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends BaseEntity<ID>, ID extends Serializable> {

    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<T> findAllActive() {
        return repository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public T getByIdActive(ID id) {
        return repository.findByIdAndActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active entity with id " + id + " not found"));
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Transactional
    public T update(ID id, T entityDetails) {
        T existingEntity = getById(id);
        return updateEntity(existingEntity, entityDetails);
    }

    @Transactional
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void softDelete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity with id " + id + " not found");
        }
        repository.softDelete(id);
    }

    @Transactional
    public void restore(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity with id " + id + " not found");
        }
        repository.restore(id);
    }

    /**
     * Update the existing entity with the details from entityDetails.
     * This method needs to be implemented by the subclass.
     *
     * @param existingEntity the existing entity to update
     * @param entityDetails  the details to update
     * @return the updated entity
     */
    protected abstract T updateEntity(T existingEntity, T entityDetails);
}