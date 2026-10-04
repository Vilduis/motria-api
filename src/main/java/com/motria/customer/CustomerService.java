package com.motria.customer;

import com.motria.security.CurrentUser;
import com.motria.serviceorder.ServiceOrderRepository;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.vehicle.VehicleRepository;
import com.motria.workshop.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final WorkshopService workshopService;
    private final CurrentUser currentUser;

    public List<CustomerResponse> findAll() {
        return customerRepository.findAllByWorkshopIdOrderByCreatedAtDesc(currentUser.workshopId()).stream()
                .map(CustomerResponse::from)
                .toList();
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getInCurrentWorkshop(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setWorkshop(workshopService.currentWorkshopReference());
        applyChanges(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getInCurrentWorkshop(id);
        applyChanges(customer, request);
        return CustomerResponse.from(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getInCurrentWorkshop(id);
        if (vehicleRepository.existsByCustomerId(id) || serviceOrderRepository.existsByCustomerId(id)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene vehículos u órdenes registradas");
        }
        customerRepository.delete(customer);
    }

    /** Busca un cliente del taller actual. Lo usan otros módulos (vehículos, órdenes). */
    public Customer getInCurrentWorkshop(Long id) {
        return customerRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el cliente", id));
    }

    private void applyChanges(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
    }
}
