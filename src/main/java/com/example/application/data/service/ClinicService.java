package com.example.application.data.service;

import com.example.application.data.entity.Clinic;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ClinicService {

    private final ClinicRepository repository;

    public ClinicService(ClinicRepository repository) {
        this.repository = repository;
    }

    public Optional<Clinic> get(Long id) {
        return repository.findById(id);
    }

    public Clinic update(Clinic entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Clinic> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Clinic> list(Pageable pageable, Specification<Clinic> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
