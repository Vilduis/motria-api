-- Un email de cliente no puede repetirse dentro del mismo taller.
-- El email es opcional: varios clientes pueden no tenerlo.

-- Normalizar los datos existentes igual que lo hace CustomerService.
UPDATE customers SET email = NULL WHERE trim(email) = '';
UPDATE customers SET email = lower(trim(email)) WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uk_customers_workshop_email
    ON customers (workshop_id, lower(email))
    WHERE email IS NOT NULL;
