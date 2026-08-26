package com.example.booking.service;

import com.example.booking.dto.ReservationDto;
import com.example.booking.dto.ReservationFilterDto;
import com.example.booking.entity.*;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ResourceRepository resourceRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public Page<ReservationDto> findAll(ReservationFilterDto filter, Pageable pageable,
                                        String username, Role role) {
        Specification<Reservation> spec = Specification.where(null);

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), ReservationStatus.valueOf(filter.getStatus())));
        }
        if (filter.getMinPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
        }
        if (role == Role.USER) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("user").get("username"), username));
        }

        Page<Reservation> page = reservationRepository.findAll(spec, pageable);
        List<ReservationDto> dtos = page.getContent().stream()
                .map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public ReservationDto create(ReservationDto dto, String username) {
        if (dto.getStartTime() != null && dto.getEndTime() != null
                && !dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + dto.getResourceId()));
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setPrice(dto.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);
        return toDto(reservationRepository.save(reservation));
    }

    public ReservationDto update(Long id, ReservationDto dto, String username, Role role) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        if (role == Role.USER && !reservation.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied: you can only modify your own reservations");
        }
        if (dto.getStartTime() != null && dto.getEndTime() != null
                && !dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (dto.getResourceId() != null) {
            Resource resource = resourceRepository.findById(dto.getResourceId())
                    .orElseThrow(() -> new RuntimeException("Resource not found with id: " + dto.getResourceId()));
            reservation.setResource(resource);
        }
        if (dto.getStartTime() != null) reservation.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) reservation.setEndTime(dto.getEndTime());
        if (dto.getPrice() != null) reservation.setPrice(dto.getPrice());
        if (dto.getStatus() != null) {
            reservation.setStatus(ReservationStatus.valueOf(dto.getStatus()));
        }
        return toDto(reservationRepository.save(reservation));
    }

    public void delete(Long id, String username, Role role) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        if (role == Role.USER && !reservation.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied: you can only delete your own reservations");
        }
        reservationRepository.delete(reservation);
    }

    private ReservationDto toDto(Reservation e) {
        return new ReservationDto(e.getId(), e.getResource().getId(),
                e.getUser().getUsername(), e.getStartTime(), e.getEndTime(),
                e.getPrice(), e.getStatus().name());
    }
}

