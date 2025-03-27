package com.example.demo.common.controller;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.service.BaseService;
import com.example.demo.common.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Generic base controller that provides standard CRUD operations.
 *
 * @param <T>  the entity type
 * @param <ID> the ID type
 * @param <S>  the service type
 */
public abstract class BaseController<T extends BaseEntity<ID>, ID extends Serializable, S extends BaseService<T, ID>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        return ResponseEntity.ok(service.findAllActive());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<T>> getPage(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id) {
        return ResponseEntity.ok(service.getByIdActive(id));
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable ID id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Entity soft deleted successfully"));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse> restore(@PathVariable ID id) {
        service.restore(id);
        return ResponseEntity.ok(new ApiResponse(true, "Entity restored successfully"));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> permanentDelete(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Entity permanently deleted successfully"));
    }
}