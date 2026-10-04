package com.motria.workshop;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workshops")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopService workshopService;

    @GetMapping("/{id}")
    public WorkshopResponse getWorkshop(@PathVariable Long id) {
        return workshopService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public WorkshopResponse updateWorkshop(@PathVariable Long id, @Valid @RequestBody UpdateWorkshopRequest request) {
        return workshopService.update(id, request);
    }
}
