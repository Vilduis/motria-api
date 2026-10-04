-- Un vehículo solo puede tener una orden sin terminar (PENDIENTE o EN_PROCESO) a la vez.
-- Las órdenes TERMINADO no cuentan: forman el historial del vehículo.
CREATE UNIQUE INDEX uk_service_orders_open_vehicle
    ON service_orders (vehicle_id)
    WHERE status <> 'TERMINADO';
