package com.motria.dashboard;

import java.util.List;

/**
 * @param inProcessOrders órdenes EN_PROCESO (equivale a bahías ocupadas).
 * @param recentOrders    últimas 5 órdenes del taller.
 */
public record AdminDashboardResponse(
        long totalVehicles,
        long newVehiclesThisWeek,
        long totalCustomers,
        long newCustomersThisWeek,
        long ordersToday,
        long completedToday,
        long pendingOrders,
        long inProcessOrders,
        long completedOrders,
        List<RecentOrderResponse> recentOrders
) {
}
