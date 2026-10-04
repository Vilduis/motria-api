package com.motria.dashboard;

import com.motria.customer.CustomerRepository;
import com.motria.security.CurrentUser;
import com.motria.serviceorder.OrderStatus;
import com.motria.serviceorder.ServiceOrder;
import com.motria.serviceorder.ServiceOrderRepository;
import com.motria.technical.Technical;
import com.motria.technical.TechnicalService;
import com.motria.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final TechnicalService technicalService;
    private final CurrentUser currentUser;
    private final Clock clock;

    public AdminDashboardResponse adminDashboard() {
        Long workshopId = currentUser.workshopId();

        LocalDate today = LocalDate.now(clock);
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LocalDateTime weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1);

        return new AdminDashboardResponse(
                vehicleRepository.countByWorkshopId(workshopId),
                vehicleRepository.countByWorkshopIdAndCreatedAtBetween(workshopId, weekStart, weekEnd),
                customerRepository.countByWorkshopId(workshopId),
                customerRepository.countByWorkshopIdAndCreatedAtBetween(workshopId, weekStart, weekEnd),
                serviceOrderRepository.countByWorkshopIdAndDateBetween(workshopId, todayStart, todayEnd),
                serviceOrderRepository.countByWorkshopIdAndStatusAndDateBetween(workshopId, OrderStatus.TERMINADO, todayStart, todayEnd),
                serviceOrderRepository.countByWorkshopIdAndStatus(workshopId, OrderStatus.PENDIENTE),
                serviceOrderRepository.countByWorkshopIdAndStatus(workshopId, OrderStatus.EN_PROCESO),
                serviceOrderRepository.countByWorkshopIdAndStatus(workshopId, OrderStatus.TERMINADO),
                toRecentOrders(serviceOrderRepository.findTop5ByWorkshopIdOrderByDateDesc(workshopId)));
    }

    /** El administrador puede ver el dashboard de cualquier técnico; un técnico, solo el suyo. */
    public TechnicalDashboardResponse technicalDashboard(Long technicalId) {
        Technical technical = technicalService.getInCurrentWorkshop(technicalId);
        if (!currentUser.isAdmin() && !technical.getUser().getId().equals(currentUser.userId())) {
            throw new AccessDeniedException("Solo puedes ver tu propio dashboard");
        }

        Long workshopId = currentUser.workshopId();
        return new TechnicalDashboardResponse(
                serviceOrderRepository.countByWorkshopIdAndTechnicalId(workshopId, technicalId),
                serviceOrderRepository.countByWorkshopIdAndTechnicalIdAndStatus(workshopId, technicalId, OrderStatus.PENDIENTE),
                serviceOrderRepository.countByWorkshopIdAndTechnicalIdAndStatus(workshopId, technicalId, OrderStatus.EN_PROCESO),
                serviceOrderRepository.countByWorkshopIdAndTechnicalIdAndStatus(workshopId, technicalId, OrderStatus.TERMINADO),
                toRecentOrders(serviceOrderRepository.findTop5ByWorkshopIdAndTechnicalIdOrderByDateDesc(workshopId, technicalId)));
    }

    private List<RecentOrderResponse> toRecentOrders(List<ServiceOrder> orders) {
        return orders.stream().map(RecentOrderResponse::from).toList();
    }
}
