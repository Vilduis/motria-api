-- Una placa no puede repetirse dentro del mismo taller.

-- Normalizar los datos existentes igual que lo hace VehicleService.
UPDATE vehicles SET plate = upper(trim(plate));

ALTER TABLE vehicles
    ADD CONSTRAINT uk_vehicles_workshop_plate UNIQUE (workshop_id, plate);
