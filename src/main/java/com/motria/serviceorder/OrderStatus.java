package com.motria.serviceorder;

/** Ciclo de vida de una orden: PENDIENTE → EN_PROCESO → TERMINADO. */
public enum OrderStatus {
    PENDIENTE,
    EN_PROCESO,
    TERMINADO
}
