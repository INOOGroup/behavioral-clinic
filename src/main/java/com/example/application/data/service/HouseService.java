package com.example.application.data.service;

import com.example.application.data.entity.House;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class HouseService {

    private final HouseRepository repository;

    public HouseService(HouseRepository repository) {
        this.repository = repository;
    }

    public Optional<House> get(Long id) {
        return repository.findById(id);
    }

    public House update(House entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<House> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<House> list(Pageable pageable, Specification<House> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
