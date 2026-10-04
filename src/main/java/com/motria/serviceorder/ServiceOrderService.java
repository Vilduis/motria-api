package com.motria.serviceorder;

import com.motria.customer.CustomerService;
import com.motria.security.CurrentUser;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.technical.TechnicalService;
import com.motria.vehicle.Vehicle;
import com.motria.vehicle.VehicleService;
import com.motria.workshop.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final TechnicalService technicalService;
    private final WorkshopService workshopService;
    private final CurrentUser currentUser;
    private final Clock clock;

    public List<ServiceOrderResponse> findAll() {
        return toResponses(serviceOrderRepository.findAllByWorkshopIdOrderByDateDesc(currentUser.workshopId()));
    }

    public List<ServiceOrderResponse> findByTechnical(Long technicalId) {
        return toResponses(serviceOrderRepository
                .findAllByWorkshopIdAndTechnicalIdOrderByDateDesc(currentUser.workshopId(), technicalId));
    }

    public List<ServiceOrderResponse> findByStatus(OrderStatus status) {
        return toResponses(serviceOrderRepository
                .findAllByWorkshopIdAndStatusOrderByDateDesc(currentUser.workshopId(), status));
    }

    public ServiceOrderResponse findById(Long id) {
        return ServiceOrderResponse.from(getInCurrentWorkshop(id));
    }

    @Transactional
    public ServiceOrderResponse create(ServiceOrderRequest request) {
        ServiceOrder order = new ServiceOrder();
        order.setWorkshop(workshopService.currentWorkshopReference());
        order.setDate(request.date() != null ? request.date() : LocalDateTime.now(clock));
        order.setStatus(OrderStatus.PENDIENTE);
        applyChanges(order, request);
        return ServiceOrderResponse.from(serviceOrderRepository.save(order));
    }

    @Transactional
    public ServiceOrderResponse update(Long id, ServiceOrderRequest request) {
        ServiceOrder order = getInCurrentWorkshop(id);
        applyChanges(order, request);
        if (request.status() != null) {
            order.setStatus(request.status());
        }
        return ServiceOrderResponse.from(order);
    }

    /** El administrador puede cambiar cualquier orden; un técnico, solo las que tiene asignadas. */
    @Transactional
    public ServiceOrderResponse updateStatus(Long id, OrderStatus status) {
        ServiceOrder order = getInCurrentWorkshop(id);
        if (!currentUser.isAdmin() && !order.isAssignedTo(currentUser.userId())) {
            throw new AccessDeniedException("Solo puedes cambiar el estado de tus órdenes asignadas");
        }
        order.setStatus(status);
        return ServiceOrderResponse.from(order);
    }

    @Transactional
    public void delete(Long id) {
        serviceOrderRepository.delete(getInCurrentWorkshop(id));
    }

    private ServiceOrder getInCurrentWorkshop(Long id) {
        return serviceOrderRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("la orden de servicio", id));
    }

    /** Las búsquedas ya filtran por taller, así que no se puede asignar nada de otro taller. */
    private void applyChanges(ServiceOrder order, ServiceOrderRequest request) {
        Vehicle vehicle = vehicleService.getInCurrentWorkshop(request.vehicleId());
        if (!vehicle.getCustomer().getId().equals(request.customerId())) {
            throw new BusinessException("El vehículo no pertenece al cliente seleccionado");
        }
        order.setVehicle(vehicle);
        order.setCustomer(customerService.getInCurrentWorkshop(request.customerId()));
        order.setTechnical(technicalService.getInCurrentWorkshop(request.technicalId()));
        order.setDiagnosis(request.diagnosis());
    }

    private List<ServiceOrderResponse> toResponses(List<ServiceOrder> orders) {
        return orders.stream().map(ServiceOrderResponse::from).toList();
    }
}
