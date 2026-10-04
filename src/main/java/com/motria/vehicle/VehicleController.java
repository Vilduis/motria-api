package com.motria.vehicle;

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
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public VehicleResponse getVehicle(@PathVariable Long id) {
        return vehicleService.findById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<VehicleResponse> getVehiclesByCustomer(@PathVariable Long customerId) {
        return vehicleService.findByCustomer(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse createVehicle(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @PutMapping("/{id}")
    public VehicleResponse updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}
