package com.motria.dashboard;

import java.util.List;

/** @param myRecentOrders últimas 5 órdenes asignadas al técnico. */
public record TechnicalDashboardResponse(
        long totalMyOrders,
        long myPendingOrders,
        long myInProcessOrders,
        long myCompletedOrders,
        List<RecentOrderResponse> myRecentOrders
) {
}
