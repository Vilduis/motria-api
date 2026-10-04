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
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private static final String DUPLICATE_EMAIL = "Ya existe un cliente con ese email";

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
        String email = normalizeEmail(request.email());
        if (email != null && customerRepository.existsByWorkshopIdAndEmailIgnoreCase(currentUser.workshopId(), email)) {
            throw new BusinessException(DUPLICATE_EMAIL);
        }
        Customer customer = new Customer();
        customer.setWorkshop(workshopService.currentWorkshopReference());
        applyChanges(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getInCurrentWorkshop(id);
        String email = normalizeEmail(request.email());
        if (email != null && customerRepository.existsByWorkshopIdAndEmailIgnoreCaseAndIdNot(currentUser.workshopId(), email, id)) {
            throw new BusinessException(DUPLICATE_EMAIL);
        }
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

    public Customer getInCurrentWorkshop(Long id) {
        return customerRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el cliente", id));
    }

    private void applyChanges(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setEmail(normalizeEmail(request.email()));
    }

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
