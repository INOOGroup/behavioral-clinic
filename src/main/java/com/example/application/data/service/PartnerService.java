package com.example.application.data.service;

import com.example.application.data.entity.Partner;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PartnerService {

    private final PartnerRepository repository;

    public PartnerService(PartnerRepository repository) {
        this.repository = repository;
    }

    public Optional<Partner> get(Long id) {
        return repository.findById(id);
    }

    public Partner update(Partner entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Partner> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Partner> list(Pageable pageable, Specification<Partner> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
