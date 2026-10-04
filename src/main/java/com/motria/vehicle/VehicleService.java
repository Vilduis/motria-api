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
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private static final String DUPLICATE_PLATE = "Ya existe un vehículo con esa placa";

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
        if (vehicleRepository.existsByWorkshopIdAndPlate(currentUser.workshopId(), normalizePlate(request.plate()))) {
            throw new BusinessException(DUPLICATE_PLATE);
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setWorkshop(workshopService.currentWorkshopReference());
        applyChanges(vehicle, request);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = getInCurrentWorkshop(id);
        if (vehicleRepository.existsByWorkshopIdAndPlateAndIdNot(currentUser.workshopId(), normalizePlate(request.plate()), id)) {
            throw new BusinessException(DUPLICATE_PLATE);
        }
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

    public Vehicle getInCurrentWorkshop(Long id) {
        return vehicleRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el vehículo", id));
    }

    private void applyChanges(Vehicle vehicle, VehicleRequest request) {
        vehicle.setPlate(normalizePlate(request.plate()));
        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setCustomer(customerService.getInCurrentWorkshop(request.customerId()));
    }

    private static String normalizePlate(String plate) {
        return plate.strip().toUpperCase(Locale.ROOT);
    }
}
