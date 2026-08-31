package com.example.booking.controller;

import com.example.booking.dto.ReservationDto;
import com.example.booking.dto.ReservationFilterDto;
import com.example.booking.entity.Role;
import com.example.booking.service.ReservationService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    private String getUsername(Authentication auth) {
        if (auth == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return auth.getName();
    }

    private Role getRole(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            return Role.USER;
        }
        String authority = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("ROLE_USER");
        return Role.valueOf(authority.replace("ROLE_", ""));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<ReservationDto>> getAll(
            ReservationFilterDto filter,
            Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(service.findAll(filter, pageable,
                getUsername(authentication), getRole(authentication)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ReservationDto> create(@Valid @RequestBody ReservationDto dto,
                                                  Authentication authentication) {
        return ResponseEntity.ok(service.create(dto, getUsername(authentication)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ReservationDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody ReservationDto dto,
                                                  Authentication authentication) {
        return ResponseEntity.ok(service.update(id, dto,
                getUsername(authentication), getRole(authentication)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, getUsername(authentication), getRole(authentication));
        return ResponseEntity.noContent().build();
    }
}
