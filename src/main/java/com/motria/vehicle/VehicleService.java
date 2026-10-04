package com.motria.vehicle;

import com.motria.customer.CustomerService;
import com.motria.security.CurrentUser;
import com.motria.serviceorder.ServiceOrderRepository;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.workshop.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerService customerService;
    private final WorkshopService workshopService;
    private final CurrentUser currentUser;

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAllByWorkshopIdOrderByCreatedAtDesc(currentUser.workshopId()).stream()
                .map(VehicleResponse::from)
                .toList();
    }

    public List<VehicleResponse> findByCustomer(Long customerId) {
        return vehicleRepository.findAllByWorkshopIdAndCustomerId(currentUser.workshopId(), customerId).stream()
                .map(VehicleResponse::from)
                .toList();
    }

    public VehicleResponse findById(Long id) {
        return VehicleResponse.from(getInCurrentWorkshop(id));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setWorkshop(workshopService.currentWorkshopReference());
        applyChanges(vehicle, request);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = getInCurrentWorkshop(id);
        applyChanges(vehicle, request);
        return VehicleResponse.from(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = getInCurrentWorkshop(id);
        if (serviceOrderRepository.existsByVehicleId(id)) {
            throw new BusinessException("No se puede eliminar el vehículo porque tiene órdenes de servicio registradas");
        }
        vehicleRepository.delete(vehicle);
    }

    /** Busca un vehículo del taller actual. Lo usa el módulo de órdenes de servicio. */
    public Vehicle getInCurrentWorkshop(Long id) {
        return vehicleRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el vehículo", id));
    }

    private void applyChanges(Vehicle vehicle, VehicleRequest request) {
        vehicle.setPlate(request.plate().trim().toUpperCase());
        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setCustomer(customerService.getInCurrentWorkshop(request.customerId()));
    }
}
