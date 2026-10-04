package com.motria.serviceorder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public List<ServiceOrderResponse> getAllServiceOrders() {
        return serviceOrderService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public ServiceOrderResponse getServiceOrder(@PathVariable Long id) {
        return serviceOrderService.findById(id);
    }

    @GetMapping("/technical/{technicalId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public List<ServiceOrderResponse> getServiceOrdersByTechnical(@PathVariable Long technicalId) {
        return serviceOrderService.findByTechnical(technicalId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ServiceOrderResponse> getServiceOrdersByStatus(@PathVariable OrderStatus status) {
        return serviceOrderService.findByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public ServiceOrderResponse createServiceOrder(@Valid @RequestBody ServiceOrderRequest request) {
        return serviceOrderService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ServiceOrderResponse updateServiceOrder(@PathVariable Long id, @Valid @RequestBody ServiceOrderRequest request) {
        return serviceOrderService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public ServiceOrderResponse updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return serviceOrderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteServiceOrder(@PathVariable Long id) {
        serviceOrderService.delete(id);
    }
}
