package com.motria.technical;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/technicals")
@RequiredArgsConstructor
public class TechnicalController {

    private final TechnicalService technicalService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public List<TechnicalResponse> getAllTechnicals() {
        return technicalService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public TechnicalResponse getTechnical(@PathVariable Long id) {
        return technicalService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public TechnicalResponse createTechnical(@Valid @RequestBody CreateTechnicalRequest request) {
        return technicalService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public TechnicalResponse updateTechnical(@PathVariable Long id, @Valid @RequestBody UpdateTechnicalRequest request) {
        return technicalService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteTechnical(@PathVariable Long id) {
        technicalService.delete(id);
    }
}
