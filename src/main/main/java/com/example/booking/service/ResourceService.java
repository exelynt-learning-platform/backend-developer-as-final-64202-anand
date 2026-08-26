package com.example.booking.service;

import com.example.booking.dto.ResourceDto;
import com.example.booking.entity.Resource;
import com.example.booking.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public List<ResourceDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ResourceDto create(ResourceDto dto) {
        Resource entity = new Resource();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return toDto(repository.save(entity));
    }

    public ResourceDto update(Long id, ResourceDto dto) {
        Resource entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return toDto(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Resource not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private ResourceDto toDto(Resource e) {
        return new ResourceDto(e.getId(), e.getName(), e.getDescription());
    }
}

