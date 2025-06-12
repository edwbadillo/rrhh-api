CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- RESET
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS contract CASCADE;
DROP TABLE IF EXISTS contract_type CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS document_type CASCADE;
DROP TABLE IF EXISTS company_branch CASCADE;
DROP TABLE IF EXISTS company CASCADE;
DROP TABLE IF EXISTS auth_user CASCADE;

CREATE TABLE auth_user (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    disabled_at TIMESTAMPTZ,
    
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'RH', 'EMPLOYEE')),
    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE company (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    legal_name VARCHAR(255) NOT NULL UNIQUE,
    trade_name VARCHAR(255),
    tax_id VARCHAR(50) NOT NULL UNIQUE,

    email VARCHAR(255),
    phone_number VARCHAR(30),
    address TEXT,

    city_name VARCHAR(100) NOT NULL,
    state_name VARCHAR(100) NOT NULL,
    country_name VARCHAR(100) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_by UUID REFERENCES auth_user(id),
    updated_by UUID REFERENCES auth_user(id)
);

CREATE TABLE company_branch (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    company_id UUID NOT NULL REFERENCES company(id),

    name VARCHAR(255) NOT NULL,

    email VARCHAR(255),
    phone_number VARCHAR(30),
    address TEXT,

    city_name VARCHAR(100) NOT NULL,
    state_name VARCHAR(100) NOT NULL,
    country_name VARCHAR(100) NOT NULL,

    is_main_branch BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_by UUID REFERENCES auth_user(id),
    updated_by UUID REFERENCES auth_user(id)
);

-- Restricción: no puede haber dos sucursales con el mismo nombre en la misma empresa
CREATE UNIQUE INDEX uq_branch_company_name ON company_branch(company_id, name);

-- Restricción: solo una sucursal principal por empresa
CREATE UNIQUE INDEX uq_branch_main_branch_per_company ON company_branch(company_id)
WHERE is_main_branch = true;


CREATE TABLE document_type (
    id SMALLINT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    description VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE employee (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    auth_user_id UUID NOT NULL REFERENCES auth_user(id),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    
    dni VARCHAR(50) NOT NULL UNIQUE,
    document_type_id SMALLINT NOT NULL REFERENCES document_type(id),

    date_of_birth DATE,
    phone_number VARCHAR(30),

    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_by UUID REFERENCES auth_user(id),
    updated_by UUID REFERENCES auth_user(id)
);

CREATE TABLE contract_type (
    id SMALLINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE contract (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    employee_id UUID NOT NULL REFERENCES employee(id),
    branch_id UUID NOT NULL REFERENCES company_branch(id),

    contract_number VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE,
    contract_type_id SMALLINT REFERENCES contract_type(id),
    position VARCHAR(100) NOT NULL,
    salary NUMERIC(12,2) NOT NULL,
    work_schedule VARCHAR(255),

    status CHAR(1) NOT NULL CHECK (status IN ('A', 'T', 'S', 'C')),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_by UUID REFERENCES auth_user(id),
    updated_by UUID REFERENCES auth_user(id)
);

CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    contract_id UUID NOT NULL REFERENCES contract(id),

    payment_month DATE NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    days_worked SMALLINT NOT NULL,

    payment_date DATE NOT NULL,
    payment_reference VARCHAR(8) NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_by UUID REFERENCES auth_user(id),
    updated_by UUID REFERENCES auth_user(id)
);


--- INSERT DE DATOS
INSERT INTO document_type (id, code, description, is_active) VALUES
(1, 'CC', 'Cédula de Ciudadanía', true),
(2, 'TI', 'Tarjeta de Identidad', true),
(3, 'CE', 'Cédula de Extranjería', true),
(4, 'PP', 'Pasaporte', true),
(5, 'PEP', 'Permiso Especial de Permanencia', true),
(6, 'VISA', 'Visa', true),
(7, 'RC', 'Registro Civil', true),
(8, 'NIT', 'Número de Identificación Tributaria', true);
INSERT INTO contract_type (id, code, description, is_active) VALUES
(1, 'FIJO', 'Contrato a término fijo', true),
(2, 'INDEFINIDO', 'Contrato a término indefinido', true),
(3, 'OBRA', 'Contrato por obra o labor', true),
(4, 'PRUEBA', 'Contrato a término de prueba', true),
(5, 'TEMPORAL', 'Contrato temporal', true),
(6, 'INTERINO', 'Contrato de interinidad', true),
(7, 'POR_HORA', 'Contrato por horas', true),
(8, 'SERVICIOS', 'Contrato de prestación de servicios', true);

-- SEED

-- Usuarios no empleados (roles ADMIN, RH)
INSERT INTO auth_user (id, firebase_uid, email, full_name, role)
VALUES
('11111111-1111-1111-1111-111111111111', 'firebase-uid-admin1', 'admin1@example.com', 'ADMIN USER ONE', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'firebase-uid-rh1', 'rh1@example.com', 'RH USER ONE', 'RH'),
('33333333-3333-3333-3333-333333333333', 'firebase-uid-admin2', 'admin2@example.com', 'ADMIN USER TWO', 'ADMIN');

-- Empresas
INSERT INTO company (id, legal_name, trade_name, tax_id, email, phone_number, address, city_name, state_name, country_name, created_by, updated_by)
VALUES
('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'EMPRESA UNO S.A.', 'Empresa Uno', '900123456-1', 'contacto@empresauno.com', '3101234567', 'Calle 123 #45-67', 'Bogotá', 'Cundinamarca', 'Colombia', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'EMPRESA DOS S.A.S.', 'Empresa Dos', '900765432-1', 'contacto@empresados.com', '3107654321', 'Carrera 45 #12-34', 'Medellín', 'Antioquia', 'Colombia', '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222');

-- Sucursales (2 por empresa)
INSERT INTO company_branch (id, company_id, name, email, phone_number, address, city_name, state_name, country_name, is_main_branch, created_by, updated_by)
VALUES
('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Sede Principal', 'principal@empresauno.com', '3101111111', 'Av. Principal 1', 'Bogotá', 'Cundinamarca', 'Colombia', true, '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Sucursal Norte', 'norte@empresauno.com', '3102222222', 'Av. Norte 123', 'Bogotá', 'Cundinamarca', 'Colombia', false, '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),

('bbbbbbb3-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Sede Principal', 'principal@empresados.com', '3103333333', 'Calle Principal 45', 'Medellín', 'Antioquia', 'Colombia', true, '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222'),
('bbbbbbb4-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Sucursal Sur', 'sur@empresados.com', '3104444444', 'Calle Sur 67', 'Medellín', 'Antioquia', 'Colombia', false, '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222');

-- Empleados y sus usuarios (rol EMPLOYEE)
INSERT INTO auth_user (id, firebase_uid, email, full_name, role)
VALUES
('44444444-4444-4444-4444-444444444444', 'firebase-uid-emp1', 'empleado1@example.com', 'JUAN PEREZ', 'EMPLOYEE'),
('55555555-5555-5555-5555-555555555555', 'firebase-uid-emp2', 'empleado2@example.com', 'MARIA LOPEZ', 'EMPLOYEE'),
('66666666-6666-6666-6666-666666666666', 'firebase-uid-emp3', 'empleado3@example.com', 'CARLOS GOMEZ', 'EMPLOYEE');

INSERT INTO employee (id, auth_user_id, first_name, last_name, dni, document_type_id, date_of_birth, phone_number, created_by, updated_by)
VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', 'Juan', 'Perez', '1234567890', 1, '1985-05-15', '3001111111', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', '55555555-5555-5555-5555-555555555555', 'Maria', 'Lopez', '0987654321', 1, '1990-08-22', '3002222222', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', 'Carlos', 'Gomez', '1122334455', 3, '1988-12-01', '3003333333', '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222');

-- Contrato (para Juan Perez, en Sede Principal de Empresa Uno)
INSERT INTO contract (id, employee_id, branch_id, contract_number, start_date, end_date, contract_type_id, position, salary, work_schedule, status, created_by, updated_by)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'CONTR-0001', '2024-01-01', '2024-12-31', 1, 'Analista de Sistemas', 3500000.00, 'Lunes a Viernes, 8:00-17:00', 'A', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111');

-- Pago (para contrato CONTR-0001)
INSERT INTO payment (id, contract_id, payment_month, amount, days_worked, payment_date, payment_reference, created_by, updated_by)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '2024-01-31', 3500000.00, 22, '2024-01-31', 'PAY00001', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111');
